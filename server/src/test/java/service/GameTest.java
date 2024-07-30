package service;

import dataaccess.DataAccessException;
import dataaccess.memorydao.MemoryGameDAO;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.result.CreateResult;
import model.result.ListResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    static GameService gameService = new GameService(new MemoryGameDAO());

    @BeforeEach
    void setUp() throws DataAccessException {
        gameService.clear();
    }

    @Test
    void createGame() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("test");
        CreateResult cr = gameService.createGame(createRequest);
        assertEquals(1, gameService.listGames().games().size(), "Should have 1 game in list");
        CreateResult cr2 = gameService.createGame(createRequest);
        assertNotEquals(cr.gameID(), cr2.gameID(), "Should have different game IDs");
    }

    @Test
    void createGameBadInputs() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameService.createGame(new CreateRequest("")),
                "Should throw exception on empty game name");
        assertThrows(DataAccessException.class, () -> gameService.createGame(new CreateRequest(null)),
                "Should throw exception on null game name");
    }

    @Test
    void joinGame() throws DataAccessException {
        CreateResult cr = gameService.createGame(new CreateRequest("test"));
        gameService.joinGame(new JoinRequest(cr.gameID(), "WHITE"), "user1");
        ListResult lr = gameService.listGames();
        assertEquals(lr.games().iterator().next().whiteUsername(), "user1", "Should have user1 as white player");
        gameService.joinGame(new JoinRequest(cr.gameID(), "BLACK"), "user2");
        lr = gameService.listGames();
        assertEquals(lr.games().iterator().next().blackUsername(), "user2", "Should have user2 as black player");
    }

    @Test
    void joinGameBadInputs() throws DataAccessException {
        CreateResult cr = gameService.createGame(new CreateRequest("test"));
        JoinRequest joinRequest = new JoinRequest(cr.gameID(), "WHITE");
        assertThrows(DataAccessException.class, () -> gameService.joinGame(
                new JoinRequest(999, "WHITE"), "test"),
                "Should throw exception on unknown game ID");
        assertThrows(DataAccessException.class, () -> gameService.joinGame(
                new JoinRequest(cr.gameID(), "a"), "test"),
                "Should throw exception on bad color");
        gameService.joinGame(joinRequest, "user1");
        assertThrows(DataAccessException.class, () -> gameService.joinGame(
                joinRequest, "user2"),
                "Should throw exception on already taken color");
    }

    @Test
    void listGames() throws DataAccessException {
        assertEquals(0, gameService.listGames().games().size(), "Should have 0 games in list");
        gameService.createGame(new CreateRequest("test"));
        assertEquals(1, gameService.listGames().games().size(), "Should have 1 game in list");
    }

}