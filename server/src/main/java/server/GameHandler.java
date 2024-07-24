package server;

import model.AuthData;
import model.request.CreateRequest;
import service.AuthService;
import service.GameService;
import spark.*;
import com.google.gson.Gson;

import java.util.Map;

public class GameHandler {
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
            if (e.getMessage().equals("Error: unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return errorHandler(e, req, res, res.status());
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
            if (e.getMessage().equals("Error: unauthorized")) {
                res.status(401);
            } else if (e.getMessage().equals("Error: bad request")) {
                res.status(400);
            } else {
                res.status(500);
            }
            return errorHandler(e, req, res, res.status());
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
            if (e.getMessage().equals("Error: unauthorized")) {
                res.status(401);
            } else if (e.getMessage().equals("Error: bad request")) {
                res.status(400);
            } else if (e.getMessage().equals("Error: already taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return errorHandler(e, req, res, res.status());
        }
    }

    public Object errorHandler(Exception e, Request req, Response res, int status) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(status);
        res.body(body);
        return body;
    }
}
