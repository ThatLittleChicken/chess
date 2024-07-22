package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;

    UserData createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void updateUser(String username, UserData user) throws DataAccessException;
}
