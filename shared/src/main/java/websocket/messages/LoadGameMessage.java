package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private final String game;

    public LoadGameMessage() {
        super(ServerMessageType.LOAD_GAME);
        this.game = "game";
    }

    public String getMessage() {
        return null;
    }
}
