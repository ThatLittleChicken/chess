package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ErrorHandler {
    public Object errorHandler(Exception e, Request req, Response res) {
        if (e.getMessage().equals("Error: bad request")) {
            res.status(400);
        } else if (e.getMessage().equals("Error: already taken")) {
            res.status(403);
        } else if (e.getMessage().equals("Error: unauthorized")) {
            res.status(401);
        } else {
            res.status(500);
        }
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.body(body);
        return body;
    }
}
