package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear() throws DataAccessException;

    GameData createGame(int gameID) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int gameID, GameData game) throws DataAccessException;
}
