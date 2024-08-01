package dataaccess.mysqldao;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO extends DatabaseFunctionHandler implements GameDAO {

    private final String[] createStatements = {
            "CREATE TABLE IF NOT EXISTS games (gameID INT NOT NULL AUTO_INCREMENT, gameName VARCHAR(255) NOT NULL, " +
                    "whiteUsername VARCHAR(255), blackUsername VARCHAR(255), gameData TEXT, PRIMARY KEY (gameID))"
    };

    public MySqlGameDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        executeUpdate(statement);
    }

    public GameData createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        var statement = "INSERT INTO games (gameName, whiteUsername, blackUsername, gameData) VALUES (?, ?, ?, ?)";
        var chessGame = new ChessGame();
        var json = new Gson().toJson(chessGame);
        var id = executeUpdate(statement, gameName, null, null, json);
        return new GameData(id, null, null, gameName, chessGame);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to get data: %s", e.getMessage()));
        }
        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to get data: %s", e.getMessage()));
        }
        return games;
    }

    public void updateGame(int gameID, GameData game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        var statement = "UPDATE games SET gameName = ?, whiteUsername = ?, blackUsername = ?, gameData = ? WHERE gameID = ?";
        var json = new Gson().toJson(game.game());
        executeUpdate(statement, game.gameName(), game.whiteUsername(), game.blackUsername(), json, game.gameID());
    }

    private GameData readGame(ResultSet rs) throws java.sql.SQLException {
        var gameID = rs.getInt("gameID");
        var gameName = rs.getString("gameName");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var json = rs.getString("gameData");
        var chessGame = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }
}
