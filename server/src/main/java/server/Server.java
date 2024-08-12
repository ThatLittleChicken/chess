package server;

import com.google.gson.Gson;
import dataaccess.memorydao.MemoryAuthDAO;
import dataaccess.memorydao.MemoryGameDAO;
import dataaccess.memorydao.MemoryUserDAO;
import dataaccess.mysqldao.MySqlAuthDAO;
import dataaccess.mysqldao.MySqlGameDAO;
import dataaccess.mysqldao.MySqlUserDAO;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;
import websocket.WebsocketHandler;

import java.util.Map;

public class Server {
    public static AuthService authService;
    public static UserService userService;
    public static GameService gameService;
    private WebsocketHandler websocketHandler;

    public Server() {
        try {
            this.authService = new AuthService(new MySqlAuthDAO());
            this.userService = new UserService(new MySqlUserDAO());
            this.gameService = new GameService(new MySqlGameDAO());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        websocketHandler = new WebsocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", websocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new UserHandler(userService, authService)::register);
        Spark.post("/session", new UserHandler(userService, authService)::login);
        Spark.delete("/session", new UserHandler(userService, authService)::logout);
        Spark.get("/game", new GameHandler(gameService, authService)::listGames);
        Spark.post("/game", new GameHandler(gameService, authService)::createGame);
        Spark.put("/game", new GameHandler(gameService, authService)::joinGame);
        Spark.delete("/db", this::clearAll);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clearAll(Request req, Response res) {
        try {
            userService.clear();
            gameService.clear();
            authService.clear();
            res.status(200);
            return "";
        } catch (Exception e) {
            res.status(500);
            res.type("application/json");
            return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
