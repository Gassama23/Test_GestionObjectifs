package orange.odk.repository.interfaces;

import orange.odk.model.User;

public interface UserInterface {
            User sauvergarder(UserInterface user);
            User trouverParEmail(String email);
            boolean emailExiste(String email);
}
