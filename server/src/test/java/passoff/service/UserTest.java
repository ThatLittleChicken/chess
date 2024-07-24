package passoff.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    static final UserService gameService = new UserService(new MemoryUserDAO());

    @BeforeEach
    void setUp() throws DataAccessException {
        gameService.clear();
    }

    @Test
    void register() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("test", "password", "email");
        RegisterResult rr = gameService.register(registerRequest, "authToken");
        assertEquals("test", rr.username(), "Should have registered user");
        assertThrows(DataAccessException.class, () -> gameService.register(new RegisterRequest("", "password", "email"), "authToken"), "Should throw exception on empty username");
        assertThrows(DataAccessException.class, () -> gameService.register(new RegisterRequest("test", "", "email"), "authToken"), "Should throw exception on empty password");
        assertThrows(DataAccessException.class, () -> gameService.register(new RegisterRequest("test", "password", ""), "authToken"), "Should throw exception on empty email");
        assertThrows(DataAccessException.class, () -> gameService.register(new RegisterRequest("test", "password", "email"), "authToken"), "Should throw exception on already taken username");
    }

    @Test
    void login() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("test", "password", "email");
        gameService.register(registerRequest, "authToken");
        assertEquals("test", gameService.login(new LoginRequest("test", "password"), "authToken").username(), "Should have logged in user");
        assertThrows(DataAccessException.class, () -> gameService.login(new LoginRequest("test", "bad"), "authToken"), "Should throw exception on bad password");
        assertThrows(DataAccessException.class, () -> gameService.login(new LoginRequest("bad", "password"), "authToken"), "Should throw exception on bad username");
    }
}
