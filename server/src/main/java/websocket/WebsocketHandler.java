package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import server.Server;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case LEAVE -> leave(session, command);
            case RESIGN -> resign(session, command);
            case CONNECT -> {
                JoinGameCommand joinGameCommand = new Gson().fromJson(message, JoinGameCommand.class);
                connect(session, joinGameCommand);
            }
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(session, makeMoveCommand);
            }
        }
    }

    public void connect(Session session, JoinGameCommand command) throws IOException {
        connectionManager.addConnection(command.getGameID(), session);
        try {
            if (Server.gameService.getGame(command.getGameID()) == null) {
                throw new Exception("Game not found");
            }
            var authData = Server.authService.getAuth(command.getAuthToken());
            NotificationMessage nm;
            if (!command.isObserver()) {
                nm = new NotificationMessage("Player " + authData.username() + " has joined the game as " + command.getColor());
            } else {
                nm = new NotificationMessage("Player " + authData.username() + " is spectating the game");
            }
            connectionManager.broadcast(session, command.getGameID(), nm);
            LoadGameMessage lgm = new LoadGameMessage();
            connectionManager.sendLoadGame(session, lgm);
        } catch (Exception e) {
            connectionManager.sendError(session, new ErrorMessage(e.getMessage()));
        }
    }

    public void leave(Session session, UserGameCommand command) throws IOException {
        connectionManager.removeConnection(session);
        try {
            var authData = Server.authService.getAuth(command.getAuthToken());
            var game = Server.gameService.getGame(command.getGameID());
            removePlayer(game, authData.username());
            NotificationMessage nm = new NotificationMessage("Player " + authData.username() + " has left the game");
            connectionManager.broadcast(session, command.getGameID(), nm);
        } catch (Exception e) {
            connectionManager.sendError(session, new ErrorMessage(e.getMessage()));
        }
    }

    public void resign(Session session, UserGameCommand command) throws IOException {
        try {
            var authData = Server.authService.getAuth(command.getAuthToken());
            var game = Server.gameService.getGame(command.getGameID());
            checkIsGameOver(game);
            if (!authData.username().equals(game.whiteUsername()) && !authData.username().equals(game.blackUsername())) {
                throw new Exception("Not playing");
            }
            game.game().setIsGameOver(true);
            Server.gameService.updateGame(command.getGameID(), game);
            NotificationMessage nm = new NotificationMessage("Player " + authData.username() + " has resigned");
            connectionManager.broadcast(null, command.getGameID(), nm);
        } catch (Exception e) {
            connectionManager.sendError(session, new ErrorMessage(e.getMessage()));
        }
    }

    public void makeMove(Session session, MakeMoveCommand command) throws IOException {
        try {
            var authData = Server.authService.getAuth(command.getAuthToken());
            var game = Server.gameService.getGame(command.getGameID());
            checkIsGameOver(game);
            checkTurn(game, authData.username());
            game.game().makeMove(command.getMove());
            Server.gameService.updateGame(command.getGameID(), game);
            NotificationMessage nm =
                    new NotificationMessage("Player " + authData.username() + " has made a move " + formatMove(command.getMove()));
            connectionManager.broadcast(session, command.getGameID(), nm);
            LoadGameMessage lgm = new LoadGameMessage();
            connectionManager.broadcast(null, command.getGameID(), lgm);
            announceChecks(game);
        } catch (Exception e) {
            connectionManager.sendError(session, new ErrorMessage(e.getMessage()));
        }
    }

    private void checkTurn(GameData gameData, String username) throws Exception {
        if (gameData.whiteUsername() != null && username.equals(gameData.whiteUsername())) {
            if (gameData.game().getTeamTurn() != ChessGame.TeamColor.WHITE) {
                throw new Exception("Currently black's turn");
            }
        } else if (gameData.blackUsername() != null && username.equals(gameData.blackUsername())) {
            if (gameData.game().getTeamTurn() != ChessGame.TeamColor.BLACK) {
                throw new Exception("Currently white's turn");
            }
        } else {
            throw new Exception("Not playing");
        }
    }

    private void checkIsGameOver(GameData gameData) throws Exception {
        if (gameData.game().getIsGameOver()) {
            throw new Exception("Game's Over");
        }
    }

    private void removePlayer(GameData gameData, String username) throws Exception {
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            Server.gameService.updateGame(gameData.gameID(), new GameData(
                    gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game())
            );
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            Server.gameService.updateGame(gameData.gameID(), new GameData(
                    gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game())
            );
        }
    }

    private void announceChecks(GameData gameData) {
        ChessGame.TeamColor teamColor = gameData.game().getTeamTurn();
        ChessGame chessGame = gameData.game();
        if (chessGame.isInCheckmate(teamColor)) {
            if (teamColor == ChessGame.TeamColor.WHITE) {
                connectionManager.broadcast(null, gameData.gameID(),
                        new NotificationMessage(gameData.whiteUsername() + " got checkmated"));
            } else {
                connectionManager.broadcast(null, gameData.gameID(),
                        new NotificationMessage(gameData.blackUsername() + " got checkmated"));
            }
        } else if (chessGame.isInCheck(teamColor)) {
             if (teamColor == ChessGame.TeamColor.WHITE) {
                 connectionManager.broadcast(null, gameData.gameID(),
                         new NotificationMessage(gameData.whiteUsername() + " is in check"));
             } else {
                 connectionManager.broadcast(null, gameData.gameID(),
                         new NotificationMessage(gameData.blackUsername() + " is in check"));
             }
         }
    }

    private String formatMove(ChessMove move) {
        return String.format("%s%s to %s%s", (char) (move.getStartPosition().getColumn() + 96), move.getStartPosition().getRow(),
                (char) (move.getEndPosition().getColumn() + 96), move.getEndPosition().getRow());
    }
}
