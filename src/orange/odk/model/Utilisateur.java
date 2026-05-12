package orange.odk.model;

import orange.odk.enum_package.EnumRole;

public class Utilisateur extends User {
	private int streakActuel,meilleurStreak;
	
	public Utilisateur(int id,String nom,String prenom,String email,String mot_de_passe) {
		super(id,nom,prenom,email,mot_de_passe,EnumRole.Utilisateur);
		this.streakActuel = 0;
		this.meilleurStreak = 0;
	}
	public Utilisateur(String nom,String prenom,String email,String mot_de_passe) {
		super(nom,prenom,email,mot_de_passe,EnumRole.Utilisateur);
		this.streakActuel = 0;
		this.meilleurStreak = 0;
	}
	public Utilisateur() {
		super();
		this.streakActuel = 0;
		this.meilleurStreak = 0;
		this.role=EnumRole.Utilisateur;
	}

	public int getStreakActuel() {
		return streakActuel;
	}

	public void setStreakActuel(int streakActuel) {
		this.streakActuel = streakActuel;
	}

	public int getMeilleurStreak() {
		return meilleurStreak;
	}

	public void setMeilleurStreak(int meilleurStreak) {
		this.meilleurStreak = meilleurStreak;
	}
	@Override
	public void seConnecter() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void inscrire() {
		// TODO Auto-generated method stub
		
	}
	
	
}
