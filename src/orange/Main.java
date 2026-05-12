package orange;

import java.sql.Connection;

import orange.odk.model.Utilisateur;

public class Main {

	public static void main(String[] args) {
           Connection connection=DataBaseConnection.getConnection();		
		Utilisateur user1=new Utilisateur();
		//user1.setMeilleurStreak(56);
		//user1.meilleurStreak=2443;
		//user1.setMeilleurStreak(1);
		System.out.println("le streak de user1 est :"+user1.getRole());
		
		
		

	}

}
