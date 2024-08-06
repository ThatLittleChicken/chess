package client;

import dataaccess.DataAccessException;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.ListResult;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static final RegisterRequest RR = new RegisterRequest("player1", "password", "p1email.com");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearDB() throws Exception {
        facade.clearAll();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register(RR);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerError() throws Exception {
        facade.register(RR);
        assertThrows(Exception.class, () -> facade.register(RR), "Should throw on duplicate user");
        RegisterRequest rr2 = new RegisterRequest("", "password", "p1email.com");
        assertThrows(Exception.class, () -> facade.register(rr2), "Should throw on empty username");
        RegisterRequest rr3 = new RegisterRequest("player1", "", "p1email.com");
        assertThrows(Exception.class, () -> facade.register(rr3), "Should throw on empty password");
        RegisterRequest rr4 = new RegisterRequest("player1", "password", "");
        assertThrows(Exception.class, () -> facade.register(rr4), "Should throw on empty email");
    }

    @Test
    void login() throws Exception {
        facade.register(RR);
        LoginRequest lr = new LoginRequest("player1", "password");
        var loginResult = facade.login(lr);
        assertTrue(loginResult.authToken().length() > 10);
    }

    @Test
    void loginError() throws Exception {
        facade.register(RR);
        LoginRequest lr = new LoginRequest("player1", "wrongpassword");
        assertThrows(Exception.class, () -> facade.login(lr), "Should throw on bad password");
        LoginRequest lr2 = new LoginRequest("player1", "");
        assertThrows(Exception.class, () -> facade.login(lr2), "Should throw on empty password");
        LoginRequest lr3 = new LoginRequest("", "password");
        assertThrows(Exception.class, () -> facade.login(lr3), "Should throw on empty username");
    }

    @Test
    void logout() throws Exception {
        var rr = facade.register(RR);
        assertDoesNotThrow(() -> facade.logout(rr.authToken()), "Should not throw on logout");
    }

    @Test
    void logoutError() {
        assertThrows(Exception.class, () -> facade.logout("badtoken"), "Should throw on bad token");
        assertThrows(Exception.class, () -> facade.logout(""), "Should throw on empty token");
    }

    @Test
    void createGame() throws Exception {
        var rr = facade.register(RR);
        var cr = new CreateRequest("game1");
        assertDoesNotThrow(() -> facade.createGame(cr, rr.authToken()), "Should not throw on create game");
    }

    @Test
    void createGameError() throws Exception {
        var rr = facade.register(RR);
        var cr = new CreateRequest("");
        assertThrows(Exception.class, () -> facade.createGame(cr, rr.authToken()), "Should throw on empty game name");
        assertThrows(Exception.class, () -> facade.createGame(cr, "badtoken"), "Should throw on bad token");
    }

    @Test
    void listGames() throws Exception {
        var rr = facade.register(RR);
        var cr = new CreateRequest("game1");
        facade.createGame(cr, rr.authToken());
        assertDoesNotThrow(() -> facade.listGames(rr.authToken()), "Should not throw on list games");
        ListResult lr2 = facade.listGames(rr.authToken());
        assertFalse(lr2.games().isEmpty(), "Should have at least one game");
        assertEquals("game1", lr2.games().stream().filter(g -> g.gameName().equals("game1")).findFirst().get().gameName(),
                "Should have game1");
    }

    @Test
    void listGamesError() {
        assertThrows(Exception.class, () -> facade.listGames("badtoken"), "Should throw on bad token");
        assertThrows(Exception.class, () -> facade.listGames(""), "Should throw on empty token");
    }

    @Test
    void joinGame() throws Exception {
        var rr = facade.register(RR);
        var cr = new CreateRequest("game1");
        facade.createGame(cr, rr.authToken());
        ListResult lr2 = facade.listGames(rr.authToken());
        var gameID = lr2.games().stream().filter(g -> g.gameName().equals("game1")).findFirst().get().gameID();
        JoinRequest jr = new JoinRequest(gameID, "WHITE");
        assertDoesNotThrow(() -> facade.joinGame(jr, rr.authToken()), "Should not throw on join game");
        ListResult lr3 = facade.listGames(rr.authToken());
        assertEquals("player1", lr3.games().stream().filter(g -> g.gameID() == gameID).findFirst().get().whiteUsername(),
                "Should have player1 as white");
    }

    @Test
    void joinGameError() throws Exception {
        var rr = facade.register(RR);
        var cr = new CreateRequest("game1");
        facade.createGame(cr, rr.authToken());
        ListResult lr2 = facade.listGames(rr.authToken());
        var gameID = lr2.games().stream().filter(g -> g.gameName().equals("game1")).findFirst().get().gameID();
        JoinRequest jr = new JoinRequest(gameID, "WHITE");
        assertThrows(Exception.class, () -> facade.joinGame(jr, "badtoken"), "Should throw on bad token");
        JoinRequest jr2 = new JoinRequest(0, "WHITE");
        assertThrows(Exception.class, () -> facade.joinGame(jr2, rr.authToken()), "Should throw on bad game ID");
        JoinRequest jr3 = new JoinRequest(gameID, "BADCOLOR");
        assertThrows(Exception.class, () -> facade.joinGame(jr3, rr.authToken()), "Should throw on bad color");
        JoinRequest jr4 = new JoinRequest(gameID, "WHITE");
        facade.joinGame(jr4, rr.authToken());
        assertThrows(Exception.class, () -> facade.joinGame(jr, rr.authToken()), "Should throw on taken color");
    }
}
