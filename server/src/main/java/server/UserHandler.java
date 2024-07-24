package server;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import service.AuthService;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

import static spark.Spark.halt;

public class UserHandler extends ErrorHandler {
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
            return errorHandler(e, req, res);
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
            return errorHandler(e, req, res);
        }
    }

    public Object logout(Request req, Response res) {
        try {
            authService.getAuth(req.headers("authorization"));
            authService.deleteAuth(req.headers("authorization"));
            res.status(200);
            return "";
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }
}
