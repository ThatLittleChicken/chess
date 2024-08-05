package ui;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.ListResult;
import model.result.LoginResult;
import model.result.RegisterResult;

import java.util.Arrays;
import java.util.HashMap;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = State.LOGGED_OUT;
    private final HashMap<Integer, Integer> listToGameID = new HashMap<>();
    private String authToken;
    private GameData currentGame;

    public ChessClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> throw new DataAccessException("Unknown command, try 'help'");
            };
        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        String result = "";
        if (state == State.LOGGED_OUT) {
            result = EscapeSequences.SET_TEXT_COLOR_BLUE + "  register <USERNAME> <PASSWORD> <EMAIL>" +
                    EscapeSequences.RESET_TEXT_COLOR + " - to create a new account\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  login <USERNAME> <PASSWORD>" +
                    EscapeSequences.RESET_TEXT_COLOR + " - to play chess\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  quit" +
                    EscapeSequences.RESET_TEXT_COLOR + " - playing chess\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  help" +
                    EscapeSequences.RESET_TEXT_COLOR + " - with possible commands";
        } else if (state == State.LOGGED_IN) {
            result = EscapeSequences.SET_TEXT_COLOR_BLUE + "  create <NAME>" +
                    EscapeSequences.RESET_TEXT_COLOR + " - a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  list" +
                    EscapeSequences.RESET_TEXT_COLOR + " - games\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  join <ID> [WHITE|BLACK]" +
                    EscapeSequences.RESET_TEXT_COLOR + " - a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  logout" +
                    EscapeSequences.RESET_TEXT_COLOR + " - when you're done\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  help" +
                    EscapeSequences.RESET_TEXT_COLOR + " - with possible commands";
        }
        return result;
    }

    public String register(String... params) throws DataAccessException {
        assertState(State.LOGGED_OUT);
        if (params.length == 3) {
            RegisterRequest rreq = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult rres = serverFacade.register(rreq);
            authToken = rres.authToken();
            state = State.LOGGED_IN;
        } else {
            throw new DataAccessException("Invalid input: register <USERNAME> <PASSWORD> <EMAIL>");
        }
        return "Registered new user as " + params[0];
    }

    public String login(String... params) throws DataAccessException {
        assertState(State.LOGGED_OUT);
        if (params.length == 2) {
            LoginRequest lreq = new LoginRequest(params[0], params[1]);
            LoginResult lres = serverFacade.login(lreq);
            authToken = lres.authToken();
            state = State.LOGGED_IN;
        } else {
            throw new DataAccessException("Invalid input: login <USERNAME> <PASSWORD>");
        }
        return "Logged in as " + params[0];
    }

    public String getState() {
        return state.toString();
    }

    public GameData getCurrentGame() {
        return currentGame;
    }

    private void assertState(State state) throws DataAccessException {
        if (this.state != state) {
            throw new DataAccessException("Unknown command, try 'help'");
        }
    }
}
