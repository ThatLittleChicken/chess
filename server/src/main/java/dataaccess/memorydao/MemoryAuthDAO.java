package dataaccess.memorydao;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> auths = new HashMap<>();

    public void clear() throws DataAccessException {
        auths.clear();
    }

    public AuthData createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
        return auth;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }
}
