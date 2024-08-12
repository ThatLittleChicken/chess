package websocket.commands;

import chess.ChessGame;

public class JoinGameCommand extends UserGameCommand {
    private boolean observer;
    private ChessGame.TeamColor color;

    public JoinGameCommand(String authToken, int gameID, ChessGame.TeamColor color) {
        super(CommandType.CONNECT, authToken, gameID);
        if (color == null) {
            this.observer = true;
        } else {
            this.observer = false;
            this.color = color;
        }
    }

    public boolean isObserver() {
        return observer;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
