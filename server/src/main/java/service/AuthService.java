package service;

import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData getAuth(String token) throws DataAccessException {
        return authDAO.getAuth(token);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
        authDAO.createAuth(authData);
        return authData;
    }

    public void deleteAuth(String token) throws DataAccessException {
        authDAO.deleteAuth(token);
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
    }
}
