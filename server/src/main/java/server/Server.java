package server;

import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private AuthService authService;
    private UserService userService;
    private GameService gameService;

    public Server() {
        this.authService = new AuthService(new dataaccess.MemoryAuthDAO());
        this.userService = new UserService(new dataaccess.MemoryUserDAO());
        this.gameService = new GameService(new dataaccess.MemoryGameDAO());
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new UserHandler(userService, authService)::register);
        Spark.post("/session", new UserHandler(userService, authService)::login);
        Spark.delete("/session", new UserHandler(userService, authService)::logout);
        Spark.get("/game", new GameHandler(gameService, authService)::listGames);
        Spark.post("/game", new GameHandler(gameService, authService)::createGame);
        Spark.put("/game", new GameHandler(gameService, authService)::joinGame);
        Spark.delete("/db", this::clearAll);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

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
            return e.getMessage();
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
