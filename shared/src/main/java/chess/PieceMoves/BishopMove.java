package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class BishopMove extends PieceMove {

    public BishopMove(ChessGame.TeamColor myColor, ChessBoard board, ChessPosition myPos) {
        super(myColor, board, myPos);
    }

    @Override
    public HashSet<ChessMove> pieceMove() {
        int row = myPos.getRow();
        int col = myPos.getColumn();

        for (int i = 1; row + i <= 8 && col + i <= 8; i++) {
            if (isValidPos(row + i, col + i)) {
                addMoves(row + i, col + i);
                if (isEnemy(row + i, col + i)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; row - i >= 1 && col - i >= 1; i++) {
            if (isValidPos(row - i, col - i)) {
                addMoves(row - i, col - i);
                if (isEnemy(row - i, col - i)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; row + i <= 8 && col - i >= 1; i++) {
            if (isValidPos(row + i, col - i)) {
                addMoves(row + i, col - i);
                if (isEnemy(row + i, col - i)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; row - i >= 1 && col + i <= 8; i++) {
            if (isValidPos(row - i, col + i)) {
                addMoves(row - i, col + i);
                if (isEnemy(row - i, col + i)) {
                    break;
                }
            } else {
                break;
            }
        }

        return moves;
    }
}
