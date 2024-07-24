package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    int id = 0;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public void clear() throws DataAccessException {
        games.clear();
    }

    public GameData createGame(String gameName) throws DataAccessException {
        GameData game = new GameData(++id, null, null, gameName, new ChessGame());
        games.put(game.gameID(), game);
        return game;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    public void updateGame(int gameID, GameData game) throws DataAccessException {
        games.put(gameID, game);
    }
}
