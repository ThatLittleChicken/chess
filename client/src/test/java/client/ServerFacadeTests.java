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
    private static RegisterRequest RR = new RegisterRequest("player1", "password", "p1email.com");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearDB() throws DataAccessException {
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


}
