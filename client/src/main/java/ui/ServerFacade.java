package ui;

import com.google.gson.Gson;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.CreateResult;
import model.result.ListResult;
import model.result.LoginResult;
import model.result.RegisterResult;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public RegisterResult register(RegisterRequest req) throws Exception {
        return makeRequest("POST", "/user", null, req, RegisterResult.class);
    }

    public LoginResult login(LoginRequest req) throws Exception {
        return makeRequest("POST", "/session", null, req, LoginResult.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", authToken,null, null);
    }

    public ListResult listGames(String authToken) throws Exception {
        return makeRequest("GET", "/game", authToken, null, ListResult.class);
    }

    public CreateResult createGame(CreateRequest req, String authToken) throws Exception {
        return makeRequest("POST", "/game", authToken, req, CreateResult.class);
    }

    public void joinGame(JoinRequest req, String authToken) throws Exception {
        makeRequest("PUT", "/game", authToken, req, null);
    }

    public void clearAll() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }

    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("Authorization", authToken);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new Exception("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
