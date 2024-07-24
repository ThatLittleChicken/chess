package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    static UserService userService = new UserService(new MemoryUserDAO());

    @BeforeEach
    void setUp() throws DataAccessException {
        userService.clear();
    }

    @Test
    void register() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("test", "password", "email");
        RegisterResult rr = userService.register(registerRequest, "authToken");
        assertEquals("test", rr.username(), "Should have registered username");
        assertEquals("authToken", rr.authToken(), "Should have new auth token");
    }

    @Test
    void registerBadInputs() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> userService.register(
                new RegisterRequest("", "password", "email"), "authToken"),
                "Should throw exception on empty username");
        assertThrows(DataAccessException.class, () -> userService.register(
                new RegisterRequest("test", "", "email"), "authToken"),
                "Should throw exception on empty password");
        assertThrows(DataAccessException.class, () -> userService.register(
                new RegisterRequest("test", "password", ""), "authToken"),
                "Should throw exception on empty email");
        userService.register(new RegisterRequest("test", "password", "email"), "authToken");
        assertThrows(DataAccessException.class, () -> userService.register(
                new RegisterRequest("test", "password", "email"), "authToken"),
                "Should throw exception on already taken username");
    }

    @Test
    void login() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("test", "password", "email");
        userService.register(registerRequest, "authToken");
        assertEquals("test", userService.login(
                new LoginRequest("test", "password"), "authToken").username(),
                "Should have logged in user");
    }

    @Test
    void loginBadInputs() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> userService.login(
                new LoginRequest("", "password"), "authToken"),
                "Should throw exception on empty username");
        assertThrows(DataAccessException.class, () -> userService.login(
                new LoginRequest("test", ""), "authToken"),
                "Should throw exception on empty password");

        RegisterRequest registerRequest = new RegisterRequest("test", "password", "email");
        userService.register(registerRequest, "authToken");
        assertThrows(DataAccessException.class, () -> userService.login(
                        new LoginRequest("test", "bad"), "authToken"),
                "Should throw exception on wrong password");
        assertThrows(DataAccessException.class, () -> userService.login(
                        new LoginRequest("bad", "password"), "authToken"),
                "Should throw exception on unknown username");
    }
}
