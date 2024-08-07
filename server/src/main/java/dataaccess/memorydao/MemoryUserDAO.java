package dataaccess.memorydao;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public void clear() throws DataAccessException {
        users.clear();
    }

    public UserData createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
        return user;
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
}
