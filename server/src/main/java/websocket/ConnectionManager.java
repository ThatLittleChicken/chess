package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void broadcast(Session exludedSession, int gameId, NotificationMessage nm) {
        connections.forEach((session, id) -> {
            if (id == gameId && session != exludedSession) {
                if (session.isOpen()) {
                    try {
                        session.getRemote().sendString(new Gson().toJson(nm));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void broadcast(Session exludedSession, int gameId, LoadGameMessage lgm) {
        connections.forEach((session, id) -> {
            if (id == gameId && session != exludedSession) {
                if (session.isOpen()) {
                    try {
                        session.getRemote().sendString(new Gson().toJson(lgm));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void sendError(Session session, ErrorMessage em) throws IOException {
        session.getRemote().sendString(new Gson().toJson(em));
    }

    public void sendLoadGame(Session session, LoadGameMessage lgm) throws IOException {
        session.getRemote().sendString(new Gson().toJson(lgm));
    }

    public void addConnection(int gameId, Session session) {
        connections.put(session, gameId);
    }

    public void removeConnection(Session session) {
        connections.remove(session);
    }
}
