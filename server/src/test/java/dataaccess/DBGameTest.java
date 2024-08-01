package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.mysqldao.MySqlGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

class DBGameTest {

    private static MySqlGameDAO mySqlGameDAO;

    static {
        try {
            mySqlGameDAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        mySqlGameDAO.clear();
    }

    @Test
    void createGame() throws DataAccessException {
        var gd = mySqlGameDAO.createGame("game1");
        var gd2 = mySqlGameDAO.createGame("game2");
        Assertions.assertNotNull(gd, "Should have created game");
        Assertions.assertTrue(gd.gameID() > 0, "Should have created game with ID");
        Assertions.assertNotEquals(gd.gameID(), gd2.gameID(), "Should have different game IDs");
        Assertions.assertEquals(new ChessGame().getBoard(), gd.game().getBoard(), "Should have created game with new chess game");
    }

    @Test
    void createGameErrors() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> mySqlGameDAO.createGame(null),
                "Should throw exception on null game name");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlGameDAO.createGame(""),
                "Should throw exception on empty game name");
    }

    @Test
    void getGame() throws DataAccessException {
        var gd = mySqlGameDAO.createGame("game1");
        var gd2 = mySqlGameDAO.getGame(gd.gameID());
        Assertions.assertEquals(gd.game().getBoard(), gd2.game().getBoard(), "Should have gotten game");
        Assertions.assertEquals("game1", gd2.gameName(), "Should have gotten game with name");
        Assertions.assertEquals(new ChessGame().getBoard(), gd2.game().getBoard(), "Should have gotten game with new chess game");
    }

    @Test
    void getGameErrors() throws DataAccessException {
        Assertions.assertNull(mySqlGameDAO.getGame(0), "Should throw exception on zero game ID");
    }

    @Test
    void listGames() throws DataAccessException {
        var gd = mySqlGameDAO.createGame("game1");
        var gd2 = mySqlGameDAO.createGame("game2");
        var games = mySqlGameDAO.listGames();
        Assertions.assertEquals(2, games.size(), "Should have listed all games");
        Assertions.assertTrue(games.iterator().next().game().getBoard().equals(gd.game().getBoard()),
                "Should have listed all games");
        Assertions.assertTrue(games.iterator().next().game().getBoard().equals(gd2.game().getBoard()),
                "Should have listed all games");
    }

    @Test
    void listGamesErrors() throws DataAccessException {
        var games = mySqlGameDAO.listGames();
        Assertions.assertEquals(0, games.size(), "Should have listed no games");
    }

    @Test
    void updateGame() throws DataAccessException, InvalidMoveException {
        var gd = mySqlGameDAO.createGame("game1");
        var chessGame = new ChessGame();
        chessGame.makeMove(new ChessMove(new ChessPosition(2, 2), new ChessPosition(4, 2), null));
        var gd2 = new GameData(gd.gameID(), gd.whiteUsername(), "newPlayer", gd.gameName(), chessGame);
        mySqlGameDAO.updateGame(gd.gameID(), gd2);
        var gd3 = mySqlGameDAO.getGame(gd.gameID());
        var cg = gd3.game();
        Assertions.assertNotEquals(gd.game().getBoard(), gd2.game().getBoard(), "Should have updated game");
        Assertions.assertEquals(chessGame.getBoard(), gd3.game().getBoard(), "Should have updated game with new chess game");
        Assertions.assertEquals("newPlayer", gd3.blackUsername(), "Should have updated game with new player");
    }

    @Test
    void updateGameErrors() throws DataAccessException {
        var gd = mySqlGameDAO.createGame("game1");
        Assertions.assertThrows(DataAccessException.class, () -> mySqlGameDAO.updateGame(gd.gameID(), null),
                "Should throw exception on null game");
    }
}
