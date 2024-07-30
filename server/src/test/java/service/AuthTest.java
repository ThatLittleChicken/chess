package service;

import dataaccess.DataAccessException;
import dataaccess.memorydao.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthTest {
    static AuthService authService = new AuthService(new MemoryAuthDAO());

    @BeforeEach
    void clear() throws DataAccessException {
        authService.clear();
    }

    @Test
    void createAuth() throws DataAccessException {
        AuthData ad = authService.createAuth("test");
        assertEquals(ad, authService.getAuth(ad.authToken()), "Should have created auth");
    }

    @Test
    void createAuthDuplicate() throws DataAccessException {
        AuthData ad1 = authService.createAuth("test");
        AuthData ad2 = authService.createAuth("test");
        assertNotEquals(ad1.authToken(), ad2.authToken(), "Should have different tokens");
    }

    @Test
    void getAuth() throws DataAccessException {
        AuthData ad = authService.createAuth("test");
        assertEquals(ad, authService.getAuth(ad.authToken()), "Should have gotten auth");
    }

    @Test
    void getAuthBadInputs() throws DataAccessException {
        AuthData ad = authService.createAuth("test");
        assertThrows(DataAccessException.class, () -> authService.getAuth(null), "Should throw exception on null token");
        assertThrows(DataAccessException.class, () -> authService.getAuth(""), "Should throw exception on empty token");
        assertThrows(DataAccessException.class, () -> authService.getAuth("bad"), "Should throw exception on unknown token");
    }

    @Test
    void deleteAuth() throws DataAccessException {
        AuthData ad = authService.createAuth("test");
        authService.deleteAuth(ad.authToken());
        assertThrows(DataAccessException.class, () -> authService.getAuth(ad.authToken()), "Should throw exception on deleted auth");
    }

    @Test
    void deleteAuthBadInputs() throws DataAccessException {
        AuthData ad = authService.createAuth("test");
        assertThrows(DataAccessException.class, () -> authService.deleteAuth(null), "Should throw exception on null token");
        assertThrows(DataAccessException.class, () -> authService.deleteAuth(""), "Should throw exception on empty token");
        assertThrows(DataAccessException.class, () -> authService.deleteAuth("bad"), "Should throw exception on unknown token");
    }
}
