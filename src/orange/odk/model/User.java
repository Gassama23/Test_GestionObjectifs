package orange.odk.model;

import java.time.LocalDate;

import orange.odk.enum_package.EnumRole;

public abstract class  User {
	
	protected int id;
	protected String nom,prenom,email,mot_de_passe;
	protected LocalDate date_incrisption;
	protected EnumRole role;
    public User() {
    	this.date_incrisption=LocalDate.now();
    }
	public User(int id, String nom, String prenom, String email, String mot_de_passe,EnumRole role) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.mot_de_passe = mot_de_passe;
		this.role = role;
		this.date_incrisption=LocalDate.now();
	}
	public User( String nom, String prenom, String email, String mot_de_passe,EnumRole role) {
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.mot_de_passe = mot_de_passe;
		this.role = role;
		this.date_incrisption=LocalDate.now();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMot_de_passe() {
		return mot_de_passe;
	}
	public void setMot_de_passe(String mot_de_passe) {
		this.mot_de_passe = mot_de_passe;
	}
	public LocalDate getDate_incrisption() {
		return date_incrisption;
	}
	public void setDate_incrisption(LocalDate date_incrisption) {
		this.date_incrisption = date_incrisption;
	}
	public EnumRole getRole() {
		return role;
	}
	public void setRole(EnumRole role) {
		this.role = role;
	}
	public String getNom_complet() {
		return this.nom + this.prenom;
	}
	public abstract void seConnecter();
	public abstract void inscrire();

}
