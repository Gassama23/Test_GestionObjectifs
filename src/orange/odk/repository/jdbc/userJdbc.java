package orange.odk.repository.jdbc;

import orange.odk.model.User;
import orange.odk.repository.interfaces.UserInterface;

public class userJdbc implements UserInterface {

	@Override
	public User sauvergarder(UserInterface user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User trouverParEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean emailExiste(String email) {
		// TODO Auto-generated method stub
		return false;
	}

}
