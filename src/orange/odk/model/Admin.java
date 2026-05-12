package orange.odk.model;

import orange.odk.enum_package.EnumRole;

public class Admin extends User {
	
	public Admin(int id,String nom,String prenom,String email,String mot_de_passe) {
		super(id,nom,prenom,email,mot_de_passe,EnumRole.Admin);

		
	}
	public Admin(String nom,String prenom,String email,String mot_de_passe) {
		super(nom,prenom,email,mot_de_passe,EnumRole.Admin);

		
	}
	public Admin() {
		super();
		this.role=EnumRole.Admin;
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
