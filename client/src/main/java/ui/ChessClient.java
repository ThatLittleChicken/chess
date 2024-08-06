package ui;

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
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "leave" -> leaveGame();
                case "quit" -> "quit";
                default -> throw new Exception("Unknown command, try 'help'");
            };
        } catch (Exception ex) {
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
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  observe <ID>" +
                    EscapeSequences.RESET_TEXT_COLOR + " - a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  logout" +
                    EscapeSequences.RESET_TEXT_COLOR + " - when you're done\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  help" +
                    EscapeSequences.RESET_TEXT_COLOR + " - with possible commands";
        }
        return result;
    }

    public String register(String... params) throws Exception {
        assertState(State.LOGGED_OUT);
        if (params.length == 3) {
            try {
                RegisterRequest rreq = new RegisterRequest(params[0], params[1], params[2]);
                RegisterResult rres = serverFacade.register(rreq);
                authToken = rres.authToken();
                state = State.LOGGED_IN;
            } catch (Exception e) {
                throw new Exception("Username already exists");
            }
        } else {
            throw new Exception("Invalid input: register <USERNAME> <PASSWORD> <EMAIL>");
        }
        return "Registered new user as " + params[0];
    }

    public String login(String... params) throws Exception {
        assertState(State.LOGGED_OUT);
        if (params.length == 2) {
            try {
                LoginRequest lreq = new LoginRequest(params[0], params[1]);
                LoginResult lres = serverFacade.login(lreq);
                authToken = lres.authToken();
                state = State.LOGGED_IN;
            } catch (Exception e) {
                throw new Exception("Invalid username or password");
            }
        } else {
            throw new Exception("Invalid input: login <USERNAME> <PASSWORD>");
        }
        return "Logged in as " + params[0];
    }

    public String logout() throws Exception {
        assertState(State.LOGGED_IN);
        serverFacade.logout(authToken);
        authToken = null;
        state = State.LOGGED_OUT;
        return "Logged out";
    }

    public String createGame(String... params) throws Exception {
        assertState(State.LOGGED_IN);
        if (params.length == 1) {
            CreateRequest cr = new CreateRequest(params[0]);
            serverFacade.createGame(cr, authToken);
            return "Created game " + params[0];
        } else {
            throw new Exception("Invalid input: create <NAME>");
        }
    }

    public String listGames() throws Exception {
        assertState(State.LOGGED_IN);
        StringBuilder result = new StringBuilder();
        int i = 1;
        listToGameID.clear();
        ListResult lr = serverFacade.listGames(authToken);
        for (var game : lr.games()) {
            result.append(String.format("%s. %s | White Player: %s, Black Player: %s\n",
                    i++, game.gameName(), game.whiteUsername(), game.blackUsername()));
            listToGameID.put(i-1, game.gameID());
        }
        return result.toString();
    }

    public String joinGame(String... params) throws Exception {
        assertState(State.LOGGED_IN);
        if (params.length == 2 && (params[1].equalsIgnoreCase("WHITE") || params[1].equalsIgnoreCase("BLACK"))) {
            try {
                int id = listToGameID.get(Integer.parseInt(params[0]));
                JoinRequest jr = new JoinRequest(id, params[1].toUpperCase());
                serverFacade.joinGame(jr, authToken);
                state = State.GAMEPLAY;
                ListResult lr = serverFacade.listGames(authToken);
                lr.games().stream().filter(game -> game.gameID() == id).findFirst().ifPresent(game -> currentGame = game);
                return "Joined game " + currentGame.gameName() + " as " + params[1];
            } catch (Exception e) {
                if (e.getMessage().contains("403")) {
                    throw new Exception("Color has already been taken");
                } else {
                    throw new Exception("Unknown game ID, try 'list'");
                }
            }
        } else {
            throw new Exception("Invalid input: join <ID> [WHITE|BLACK]");
        }
    }

    public String observeGame(String... params) throws Exception {
        assertState(State.LOGGED_IN);
        if (params.length == 1) {
            try {
                int id = listToGameID.get(Integer.parseInt(params[0]));
                state = State.GAMEPLAY;
                ListResult lr = serverFacade.listGames(authToken);
                lr.games().stream().filter(game -> game.gameID() == id).findFirst().ifPresent(game -> currentGame = game);
                return "Observing game " + currentGame.gameName();
            } catch (Exception e) {
                throw new Exception("Unknown game ID, try 'list'");
            }
        } else {
            throw new Exception("Invalid input: observe <ID>");
        }
    }

    public String leaveGame() throws Exception {
        assertState(State.GAMEPLAY);
        state = State.LOGGED_IN;
        currentGame = null;
        return "Left game";
    }

    public String getState() {
        return state.toString();
    }

    public GameData getCurrentGame() {
        return currentGame;
    }

    private void assertState(State state) throws Exception {
        if (this.state != state) {
            throw new Exception("Unknown command, try 'help'");
        }
    }
}
