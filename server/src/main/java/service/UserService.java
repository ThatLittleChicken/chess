package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.RegisterResult;


public class UserService {
    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authService = new AuthService(authDAO);
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req.username() == null || req.username().isEmpty() || req.password() == null || req.password().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        if (userDAO.getUser(req.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }
        UserData user = new UserData(req.username(), req.password(), req.email());
        userDAO.createUser(user);
        authService.createAuth(req.username());
        return new RegisterResult(user.username(), user.email());
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        UserData user = userDAO.getUser(req.username());
        if (user == null || !user.password().equals(req.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData ad = authService.createAuth(req.username());
        return new LoginResult(ad.authToken(), user.username());
    }

    public void logout(LogoutRequest req) throws DataAccessException {
        AuthData ad = authService.getAuth(req.authToken());
        if (ad == null || ad.username().isEmpty()) {
            throw new DataAccessException("Error: unauthorized");
        }
        authService.deleteAuth(req.authToken());
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
    }
}
