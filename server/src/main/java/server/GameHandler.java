package server;

import model.AuthData;
import model.request.CreateRequest;
import service.AuthService;
import service.GameService;
import spark.*;
import com.google.gson.Gson;

public class GameHandler extends ErrorHandler {
    private final GameService gameService;
    private final AuthService authService;

    public GameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    public Object listGames(Request req, Response res) {
        try {
            authService.getAuth(req.headers("authorization"));
            var listResult = gameService.listGames();
            res.status(200);
            res.type("application/json");
            return new Gson().toJson(listResult);
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }

    public Object createGame(Request req, Response res) {
        var createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        try {
            authService.getAuth(req.headers("authorization"));
            var listResult = gameService.createGame(createRequest);
            res.status(200);
            res.type("application/json");
            return new Gson().toJson(listResult);
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }

    public Object joinGame(Request req, Response res) {
        try {
            AuthData ad = authService.getAuth(req.headers("authorization"));
            var joinRequest = new Gson().fromJson(req.body(), model.request.JoinRequest.class);
            gameService.joinGame(joinRequest, ad.username());
            res.status(200);
            res.type("application/json");
            return "";
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }


}
