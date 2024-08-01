package dataaccess.mysqldao;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.ResultSet;

public class MySqlAuthDAO extends DatabaseFunctionHandler implements AuthDAO {

    private final String[] createStatements = {
            "CREATE TABLE IF NOT EXISTS auth (authToken VARCHAR(255) NOT NULL, username VARCHAR(255) NOT NULL, PRIMARY KEY (authToken))"
    };

    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE auth";
        executeUpdate(statement);
    }

    public AuthData createAuth(AuthData auth) throws DataAccessException {
        if (auth.authToken() == null || auth.authToken().isEmpty() || auth.username() == null || auth.username().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, auth.authToken(), auth.username());
        return auth;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to get data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        var statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    private AuthData readAuth(ResultSet rs) throws java.sql.SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }
}
