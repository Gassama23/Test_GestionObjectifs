package orange.odk;

import java.sql.SQLException;

import orange.odk.console.AppConsole;

public class Main {

	public static void main(String[] args) {
		try {
			AppConsole appConsole = new AppConsole();
			appConsole.demarrer();
		} catch (SQLException e) {
			System.out.println("Impossible de demarrer l'application : " + e.getMessage());
		}
	}

}
