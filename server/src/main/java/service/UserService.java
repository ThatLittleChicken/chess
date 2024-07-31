package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.RegisterResult;
import org.mindrot.jbcrypt.BCrypt;


public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest req, String authToken) throws DataAccessException {
        if (req.username() == null || req.username().isEmpty() || req.password() == null || req.password().isEmpty() ||
            req.email() == null || req.email().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        if (userDAO.getUser(req.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }
        String passwordHash = BCrypt.hashpw(req.password(), BCrypt.gensalt());
        UserData user = new UserData(req.username(), passwordHash, req.email());
        userDAO.createUser(user);
        return new RegisterResult(authToken, user.username());
    }

    public LoginResult login(LoginRequest req, String authToken) throws DataAccessException {
        UserData user = userDAO.getUser(req.username());
        if (user == null || !BCrypt.checkpw(req.password(), user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        return new LoginResult(authToken, user.username());
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
    }
}
