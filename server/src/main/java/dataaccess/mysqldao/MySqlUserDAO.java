package dataaccess.mysqldao;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.ResultSet;

public class MySqlUserDAO extends DatabaseFunctionHandler implements UserDAO {

    private final String[] createStatements = {
            "CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, email VARCHAR(255), password VARCHAR(255))"
    };

    public MySqlUserDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    public void clear() throws DataAccessException {
        var statement = "DELETE FROM users";
        executeUpdate(statement);
    }

    public UserData createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.email(), user.password());
        return user;
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to get data: %s", e.getMessage()));
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws java.sql.SQLException {
        var username = rs.getString("username");
        var email = rs.getString("email");
        var password = rs.getString("password");
        return new UserData(username, email, password);
    }
}
