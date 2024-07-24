package server;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import service.AuthService;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

import java.util.Map;

import static spark.Spark.halt;

public class UserHandler {
    private final UserService userService;
    private final AuthService authService;

    public UserHandler(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    public Object register(Request req, Response res) {
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        try {
            var authdata = authService.createAuth(registerRequest.username());
            var registerResult = userService.register(registerRequest, authdata.authToken());
            res.status(200);
            res.type("application/json");
            return new Gson().toJson(registerResult);
        } catch (Exception e) {
            if (e.getMessage().equals("Error: bad request")) {
                res.status(400);
            } else if (e.getMessage().equals("Error: already taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return errorHandler(e, req, res, res.status());
        }
    }

    public Object login(Request req, Response res) {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        try {
            var authdata = authService.createAuth(loginRequest.username());
            var loginResult = userService.login(loginRequest, authdata.authToken());
            res.status(200);
            res.type("application/json");
            return new Gson().toJson(loginResult);
        } catch (Exception e) {
            if (e.getMessage().equals("Error: unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return errorHandler(e, req, res, res.status());
        }
    }

    public Object logout(Request req, Response res) {
        try {
            authService.getAuth(req.headers("authorization"));
            authService.deleteAuth(req.headers("authorization"));
            res.status(200);
            return "";
        } catch (Exception e) {
            if (e.getMessage().equals("Error: unauthorized")) {
                res.status(401);
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
