package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

class AuthTest {
    static final AuthService authService = new AuthService(new MemoryAuthDAO());

    @BeforeEach
    void clear() throws DataAccessException {
        authService.clear();
    }

    @Test
    void createAuth() throws DataAccessException {
        AuthData ad = authService.createAuth("test");
        assertEquals(ad, authService.getAuth(ad.authToken()), "Should have created auth");
        assertThrows(DataAccessException.class, () -> authService.createAuth(""), "Should throw exception on empty username");
    }

    @Test
    void getAuth() throws DataAccessException {
        AuthData ad = authService.createAuth("test");
        assertEquals(ad, authService.getAuth(ad.authToken()), "Should have gotten auth");
        assertThrows(DataAccessException.class, () -> authService.getAuth(""), "Should throw exception on empty token");
        assertThrows(DataAccessException.class, () -> authService.getAuth("bad"), "Should throw exception on bad token");
    }

    @Test
    void deleteAuth() throws DataAccessException {
        AuthData ad = authService.createAuth("test");
        authService.deleteAuth(ad.authToken());
        assertThrows(DataAccessException.class, () -> authService.getAuth(ad.authToken()), "Should throw exception on deleted auth");
        assertThrows(DataAccessException.class, () -> authService.deleteAuth(""), "Should throw exception on empty token");
        assertThrows(DataAccessException.class, () -> authService.deleteAuth("bad"), "Should throw exception on bad token");
    }
}
