package dataaccess;

import dataaccess.mysqldao.MySqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

class DBUserTest {

    private static MySqlUserDAO mySqlUserDAO;

    static {
        try {
            mySqlUserDAO = new MySqlUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        mySqlUserDAO.clear();
    }

    @Test
    void createUser() throws DataAccessException {
        var rr = mySqlUserDAO.createUser(new UserData("test", "passwordHash", "email"));
        Assertions.assertNotNull(rr, "Should have created user");
        Assertions.assertEquals("test", rr.username(), "Should have created user with username");
        Assertions.assertEquals("email", rr.email(), "Should have created user with email");
        Assertions.assertEquals("passwordHash", rr.password(), "Should have created user with password hash");
    }

    @Test
    void createUserErrors() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.createUser(new UserData(null, "passwordHash", "email")),
                "Should throw exception on null username");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.createUser(new UserData("", "passwordHash", "email")),
                "Should throw exception on empty username");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.createUser(new UserData("test", null, "email")),
                "Should throw exception on null password hash");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.createUser(new UserData("test", "", "email")),
                "Should throw exception on empty password hash");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.createUser(new UserData("test", "passwordHash", null)),
                "Should throw exception on null email");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.createUser(new UserData("test", "passwordHash", "")),
                "Should throw exception on empty email");
        mySqlUserDAO.createUser(new UserData("test", "passwordHash", "email"));
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.createUser(new UserData("test", "passwordHash", "email")),
                "Should throw exception on already taken username");
    }

    @Test
    void getUser() throws DataAccessException {
        var rr = mySqlUserDAO.createUser(new UserData("test", "passwordHash", "email"));
        var rr2 = mySqlUserDAO.getUser(rr.username());
        Assertions.assertEquals(rr, rr2, "Should have gotten user");
        Assertions.assertEquals("test", rr2.username(), "Should have gotten user with username");
        Assertions.assertEquals("email", rr2.email(), "Should have gotten user with email");
        Assertions.assertEquals("passwordHash", rr2.password(), "Should have gotten user with password hash");
        var rr3 = mySqlUserDAO.getUser("bad");
        Assertions.assertNull(rr3, "Should have gotten nothing for unknown username");
    }

    @Test
    void getUserErrors() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.getUser(null),
                "Should throw exception on null username");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlUserDAO.getUser(""),
                "Should throw exception on empty username");
    }
}
