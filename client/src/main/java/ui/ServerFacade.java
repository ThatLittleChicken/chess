package ui;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.request.RegisterRequest;
import model.result.CreateResult;
import model.result.ListResult;
import model.result.LoginResult;
import model.result.RegisterResult;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String port) {
        serverUrl = "http://localhost:" + port;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        return makeRequest("POST", "/user", req, RegisterResult.class);
    }

    public LoginResult login(RegisterRequest req) throws DataAccessException {
        return makeRequest("POST", "/session", req, LoginResult.class);
    }

    public void logout() throws DataAccessException {
        makeRequest("DELETE", "/session", null, null);
    }

    public ListResult listGames() throws DataAccessException {
        return makeRequest("GET", "/game", null, ListResult.class);
    }

    public CreateResult createGame(CreateRequest req) throws DataAccessException {
        return makeRequest("POST", "/game", req, CreateResult.class);
    }

    public void joinGame(JoinRequest req) throws DataAccessException {
        makeRequest("PUT", "/game", req, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
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

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new DataAccessException("failure: " + status);
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
