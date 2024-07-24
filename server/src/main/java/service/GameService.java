package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.result.CreateResult;
import model.result.ListResult;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public CreateResult createGame(CreateRequest req) throws DataAccessException {
        if (req.gameName() == null || req.gameName().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        GameData game = gameDAO.createGame(req.gameName());
        return new CreateResult(game.gameID());
    }

    public void joinGame(JoinRequest req, String username) throws DataAccessException {
        if (req.playerColor() == null || req.playerColor().isEmpty() || req.gameID() < 0) {
            throw new DataAccessException("Error: bad request");
        }
        if (gameDAO.getGame(req.gameID()) == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData game = gameDAO.getGame(req.gameID());
        if (req.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            } else {
                gameDAO.updateGame(req.gameID(), new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
            }
        } else if (req.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            } else {
                gameDAO.updateGame(req.gameID(), new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
            }
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }

    public ListResult listGames() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        return new ListResult(games);
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
