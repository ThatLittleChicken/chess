package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.ListResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = State.LOGGED_OUT;
    private final HashMap<Integer, Integer> listToGameID = new HashMap<>();
    private String authToken;
    private GameData currentGameData;
    private ChessGame.TeamColor playerColor;
    private final BoardUI boardUI = new BoardUI();
    private final NotificationHandler notificationHandler;
    private final int port;
    private WebSocketFacade ws;


    public ChessClient(int port, NotificationHandler notificationHandler) {
        this.port = port;
        this.serverFacade = new ServerFacade(port);
        this.notificationHandler = notificationHandler;
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
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "resign" -> resignGame();
                case "move" -> makeMove(params);
                case "highlight" -> highlightMoves(params);
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
        } else if (state == State.GAMEPLAY) {
            result = EscapeSequences.SET_TEXT_COLOR_BLUE + "  move <FROM> <TO> [QUEEN|ROOK|BISHOP|KNIGHT]" +
                    EscapeSequences.RESET_TEXT_COLOR + " - a piece (third parameter being promotion choice)\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  highlight <POSITION>" +
                    EscapeSequences.RESET_TEXT_COLOR + " - possible moves for a piece\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  resign" +
                    EscapeSequences.RESET_TEXT_COLOR + " - the game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  leave" +
                    EscapeSequences.RESET_TEXT_COLOR + " - the game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  redraw" +
                    EscapeSequences.RESET_TEXT_COLOR + " - the board\n" +
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
                lr.games().stream().filter(game -> game.gameID() == id).findFirst().ifPresent(game -> currentGameData = game);
                playerColor = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
                ws = new WebSocketFacade(port, notificationHandler);
                ws.connect(authToken, id, playerColor);
                return "Joining game " + currentGameData.gameName() + " as " + params[1];
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
                lr.games().stream().filter(game -> game.gameID() == id).findFirst().ifPresent(game -> currentGameData = game);
                playerColor = null;
                ws = new WebSocketFacade(port, notificationHandler);
                ws.connect(authToken, id, null);
                return "Observing game " + currentGameData.gameName();
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
        ws.leave(authToken, currentGameData.gameID());
        currentGameData = null;
        return "Left game";
    }

    public String resignGame() throws Exception {
        assertState(State.GAMEPLAY);
        if (playerColor == null) {
            throw new Exception("Cannot resign as observer");
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Are you sure you want to resign? (y/n)" + EscapeSequences.RESET_TEXT_COLOR);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (!input.equalsIgnoreCase("y")) {
            return EscapeSequences.SET_TEXT_COLOR_BLUE + "Resignation cancelled" + EscapeSequences.RESET_TEXT_COLOR;
        }
        ws.resign(authToken, currentGameData.gameID());
        return "Resigned game";
    }

    public String makeMove(String... params) throws Exception {
        assertState(State.GAMEPLAY);
        if (playerColor == null) {
            throw new Exception("Cannot make move as observer");
        }
        if ((params.length == 2 || params.length == 3) && params[0].matches("[a-h][1-8]") && params[1].matches("[a-h][1-8]")) {
            try {
                ChessMove move;
                ChessPosition from = new ChessPosition(params[0].charAt(1) - '0', params[0].charAt(0) - ('a' - 1));
                ChessPosition to = new ChessPosition(params[1].charAt(1) - '0', params[1].charAt(0) - ('a' - 1));

                if (params.length == 3 && !params[2].isEmpty()) {
                    if (!params[2].toUpperCase().matches("QUEEN|ROOK|BISHOP|KNIGHT")) {
                        throw new Exception("Invalid input: move <FROM> <TO> <QUEEN|ROOK|BISHOP|KNIGHT>");
                    }
                    move = new ChessMove(from, to, ChessPiece.PieceType.valueOf(params[2].toUpperCase()));
                } else {
                    move = new ChessMove(from, to, null);
                }
                ws.makeMove(authToken, currentGameData.gameID(), move);
                return "Making move " + params[0] + " to " + params[1];
            } catch (Exception e) {
                throw new Exception("Invalid move");
            }
        } else {
            throw new Exception("Invalid input: move <FROM> <TO> <QUEEN|ROOK|BISHOP|KNIGHT>");
        }
    }

    public String highlightMoves(String... params) throws Exception {
        assertState(State.GAMEPLAY);
        if (params.length == 1 && params[0].matches("[a-h][1-8]")) {
            ChessPosition pos = new ChessPosition(params[0].charAt(1) - '0', params[0].charAt(0) - ('a' - 1));
            return boardUI.drawHighlightedBoard(getCurrentGameData().game(), pos, playerColor);
        } else {
            throw new Exception("Invalid input: highlight <POSITION>");
        }
    }

    public String redrawBoard() {
        return drawBoardUI();
    }

    public String getState() {
        return state.toString();
    }

    public GameData getCurrentGameData() {
        try {
            var lr = serverFacade.listGames(authToken);
            lr.games().stream().filter(game -> game.gameID() == currentGameData.gameID()).findFirst().ifPresent(game -> currentGameData = game);
            return currentGameData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void assertState(State state) throws Exception {
        if (this.state != state) {
            throw new Exception("Unknown command, try 'help'");
        }
    }

    private String drawBoardUI() {
        if (getCurrentGameData() != null && playerColor != null) {
            return "\n" + boardUI.drawBoard(getCurrentGameData().game(), playerColor);
        } else if (getCurrentGameData() != null) {
            return "\n" + boardUI.drawBoard(getCurrentGameData().game(), ChessGame.TeamColor.WHITE);
        } else {
            return "";
        }
    }
}
