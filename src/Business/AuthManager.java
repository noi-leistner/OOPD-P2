package Business;

import Business.Entities.User;
import Persistance.UserDAOSql;

public class AuthManager {

    private UserDAOSql userDAO;

    public AuthManager(UserDAOSql userDAO) {
        this.userDAO = userDAO;
    }

    public AuthResult login(String email, String password) {
        // 1. Validación de campos (Muy bien hecho)
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return AuthResult.EMPTY_FIELDS;
        }

        // 2. Intento de recuperar usuario
        User user = userDAO.getUserByEmail(email.trim());

        // Si el DAO devuelve null (porque no existe o por error de SQL)
        if (user == null) {
            System.out.println("DEBUG: EMAIL NOT FOUND");
            return AuthResult.INVALID_CREDENTIALS;
        }

        // 3. Verificación de contraseña (texto plano de momento)
        if (!password.equals(user.getPassword())) {
            System.out.println("DEBUG: USER PASSWORD NOT FOUND");
            return AuthResult.INVALID_CREDENTIALS;
        } else {
            System.out.println("USER FOUND");
            // 4. ÉXITO: Guardamos en la sesión ANTES de devolver el resultado
            SessionManager.getInstance().login(user);
            System.out.println("✅ Sesión iniciada para: " + user.getEmail());
            return AuthResult.SUCCESS;
        }
    }

    public AuthResult signUp(User user) {
        if (user == null) return AuthResult.DATABASE_ERROR;
        return userDAO.addUser(user);
    }


    public boolean deleteAccount(int userId) {
        return userDAO.deleteUser(userId);
    }

}
