package dataaccess;

import dataaccess.mysqldao.MySqlAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

class DBAuthTest {

    private static MySqlAuthDAO mySqlAuthDAO;

    static {
        try {
            mySqlAuthDAO = new MySqlAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        mySqlAuthDAO.clear();
    }

    @Test
    void createAuth() throws DataAccessException {
        var ad = mySqlAuthDAO.createAuth(new AuthData("testToken", "testUser"));
        var ad2 = mySqlAuthDAO.createAuth(new AuthData("testToken2", "testUser"));
        Assertions.assertNotEquals(ad.authToken(), ad2.authToken(), "Should have different tokens");
        Assertions.assertNotNull(ad, "Should have created auth");
        Assertions.assertEquals("testUser", ad.username(), "Username should be associated with auth token");

    }

    @Test
    void createAuthErrors() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> mySqlAuthDAO.createAuth(new AuthData(null, "test")),
                "Should throw exception on null username");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlAuthDAO.createAuth(new AuthData("", "test")),
                "Should throw exception on empty username");
    }

    @Test
    void getAuth() throws DataAccessException {
        var ad = mySqlAuthDAO.createAuth(new AuthData("testToken", "test"));
        var ad2 = mySqlAuthDAO.getAuth(ad.authToken());
        Assertions.assertEquals(ad, ad2, "Should have gotten auth");
        Assertions.assertEquals("test", ad2.username(),
                "Should have gotten username is associated with auth token");
        Assertions.assertNotNull(ad2, "Should have gotten auth");
        var ad3 = mySqlAuthDAO.getAuth("bad");
        Assertions.assertNull(ad3, "Should gotten nothing for unknown token");
    }

    @Test
    void getAuthErrors() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> mySqlAuthDAO.getAuth(null),
                "Should throw exception on null token");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlAuthDAO.getAuth(""),
                "Should throw exception on empty token");
    }

    @Test
    void deleteAuth() throws DataAccessException {
        var ad = mySqlAuthDAO.createAuth(new AuthData("testToken", "test"));
        Assertions.assertDoesNotThrow(() -> mySqlAuthDAO.deleteAuth(ad.authToken()),
                "Should not throw exception when deleting auth");
        var ad2 = mySqlAuthDAO.getAuth(ad.authToken());
        Assertions.assertNull(ad2, "Should have deleted auth");
    }

    @Test
    void deleteAuthErrors() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> mySqlAuthDAO.deleteAuth(null),
                "Should throw exception on null token");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlAuthDAO.deleteAuth(""),
                "Should throw exception on empty token");
    }
}
