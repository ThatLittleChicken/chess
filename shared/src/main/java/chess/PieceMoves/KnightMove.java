package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMove extends PieceMove {

    public KnightMove(ChessGame.TeamColor myColor, ChessBoard board, ChessPosition myPos) {
        super(myColor, board, myPos);
    }

    @Override
    public HashSet<ChessMove> pieceMove() {
        int row = myPos.getRow();
        int col = myPos.getColumn();

        if (row + 2 <= 8 && col + 1 <= 8 && isValidPos(row + 2, col + 1)) {
            addMoves(row + 2, col + 1);
        }
        if (row + 2 <= 8 && col - 1 >= 1 && isValidPos(row + 2, col - 1)) {
            addMoves(row + 2, col - 1);
        }
        if (row + 1 <= 8 && col + 2 <= 8 && isValidPos(row + 1, col + 2)) {
            addMoves(row + 1, col + 2);
        }
        if (row + 1 <= 8 && col - 2 >= 1 && isValidPos(row + 1, col - 2)) {
            addMoves(row + 1, col - 2);
        }
        if (row - 1 >= 1 && col + 2 <= 8 && isValidPos(row - 1, col + 2)) {
            addMoves(row - 1, col + 2);
        }
        if (row - 1 >= 1 && col - 2 >= 1 && isValidPos(row - 1, col - 2)) {
            addMoves(row - 1, col - 2);
        }
        if (row - 2 >= 1 && col + 1 <= 8 && isValidPos(row - 2, col + 1)) {
            addMoves(row - 2, col + 1);
        }
        if (row - 2 >= 1 && col - 1 >= 1 && isValidPos(row - 2, col - 1)) {
            addMoves(row - 2, col - 1);
        }

        return moves;
    }
}
