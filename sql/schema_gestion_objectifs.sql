-- Schema MySQL pour le projet Test_GestionObjectifs
-- Compatible avec la structure actuellement utilisee par les models/repositories.

CREATE DATABASE IF NOT EXISTS gestion_objectifs
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_objectifs;

CREATE TABLE IF NOT EXISTS utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(191) NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'UTILISATEUR') NOT NULL DEFAULT 'UTILISATEUR',
    date_inscription DATE NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT uq_utilisateurs_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS objectifs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id INT NOT NULL,
    titre VARCHAR(150) NOT NULL,
    domaine ENUM('APPRENTISSAGE', 'ECONOMIE', 'SPORT', 'DEVELOPPEMENT') NOT NULL,
    cible DECIMAL(10,2) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut ENUM('EN_COURS', 'TERMINE', 'ABANDONNE') NOT NULL DEFAULT 'EN_COURS',
    CONSTRAINT fk_objectifs_utilisateur
        FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT chk_objectifs_dates CHECK (date_fin >= date_debut)
);

CREATE TABLE IF NOT EXISTS progressions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    objectif_id INT NOT NULL,
    date_action DATE NOT NULL,
    valeur DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    reussi TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_progressions_objectif
        FOREIGN KEY (objectif_id) REFERENCES objectifs(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT chk_progressions_reussi CHECK (reussi IN (0, 1))
);

CREATE TABLE IF NOT EXISTS badges (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT uq_badges_nom UNIQUE (nom)
);

CREATE TABLE IF NOT EXISTS utilisateur_badges (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id INT NOT NULL,
    badge_id INT NOT NULL,
    date_obtention DATE NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_utilisateur_badges_utilisateur
        FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_utilisateur_badges_badge
        FOREIGN KEY (badge_id) REFERENCES badges(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uq_utilisateur_badges UNIQUE (utilisateur_id, badge_id)
);

CREATE INDEX idx_objectifs_utilisateur_id
    ON objectifs(utilisateur_id);

CREATE INDEX idx_objectifs_statut
    ON objectifs(statut);

CREATE INDEX idx_objectifs_domaine
    ON objectifs(domaine);

CREATE INDEX idx_progressions_objectif_id
    ON progressions(objectif_id);

CREATE INDEX idx_progressions_date_action
    ON progressions(date_action);

CREATE INDEX idx_utilisateur_badges_utilisateur_id
    ON utilisateur_badges(utilisateur_id);

CREATE INDEX idx_utilisateur_badges_badge_id
    ON utilisateur_badges(badge_id);

-- Jeu de donnees minimal
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role)
SELECT 'Admin', 'Systeme', 'admin@gestion-objectifs.local', 'admin123', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1
    FROM utilisateurs
    WHERE email = 'admin@gestion-objectifs.local'
);
