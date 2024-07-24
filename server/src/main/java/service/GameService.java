package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.request.ListRequest;
import model.result.CreateResult;
import model.result.ListResult;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthService authService;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authService = new AuthService(authDAO);
    }

    public CreateResult createGame(CreateRequest req) throws DataAccessException {
        if (req.gameName() == null || req.gameName().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        checkAuth(req.authToken());
        GameData game = gameDAO.createGame(req.gameName());
        return new CreateResult(game.gameID());
    }

    public void joinGame(JoinRequest req) throws DataAccessException {
        if (req.playerColor() == null || req.playerColor().isEmpty() || req.gameID() < 0) {
            throw new DataAccessException("Error: bad request");
        }
        AuthData ad = checkAuth(req.authToken());
        GameData game = gameDAO.getGame(req.gameID());
        if (game.gameName() == null) {
            throw new DataAccessException("Error: game does not exist");
        }
        if (req.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            } else {
                gameDAO.updateGame(req.gameID(), new GameData(game.gameID(), ad.username(), game.blackUsername(), game.gameName(), game.game()));
            }
        } else if (req.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            } else {
                gameDAO.updateGame(req.gameID(), new GameData(game.gameID(), game.whiteUsername(), ad.username(), game.gameName(), game.game()));
            }
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }

    public ListResult listGames(ListRequest req) throws DataAccessException {
        checkAuth(req.authToken());
        Collection<GameData> games = gameDAO.listGames();
        return new ListResult(games);
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    public AuthData checkAuth(String token) throws DataAccessException {
        AuthData ad = authService.getAuth(token);
        if (ad == null || ad.username().isEmpty()) {
            throw new DataAccessException("Error: unauthorized");
        }
        return ad;
    }
}
