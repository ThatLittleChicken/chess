package ui.websocket;


import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ErrorMessage errorMessage);

    void notify(NotificationMessage notificationMessage);

    void notify(LoadGameMessage loadGameMessage);
}