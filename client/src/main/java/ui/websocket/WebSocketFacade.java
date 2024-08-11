package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(int port, NotificationHandler notificationHandler) throws Exception {
        try {
            URI socketURI = new URI("ws://localhost:" + port + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    if (message.toLowerCase().contains("error")) {
                        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                        notificationHandler.notify(errorMessage);
                    } else if (message.equals("game")) {
                        LoadGameMessage loadGameMessage = new LoadGameMessage();
                        notificationHandler.notify(loadGameMessage);
                    } else {
                        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notificationMessage);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameId) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws Exception {
        try {
            var command = new MakeMoveCommand(authToken, gameId, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void leave(String authToken, int gameId) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void resign(String authToken, int gameId) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }
}