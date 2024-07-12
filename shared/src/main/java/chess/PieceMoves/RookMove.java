package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMove extends PieceMove {
    public RookMove(ChessGame.TeamColor myColor, ChessBoard board, ChessPosition myPos) {
        super(myColor, board, myPos);
    }

    @Override
    public HashSet<ChessMove> pieceMove() {
        int row = myPos.getRow();
        int col = myPos.getColumn();

        for (int i = row + 1; i <= 8; i++) {
            if (isValidPos(i, col)) {
                addMoves(i, col);
                if (isEnemy(i, col)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = row - 1; i >= 1; i--) {
            if (isValidPos(i, col)) {
                addMoves(i, col);
                if (isEnemy(i, col)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = col + 1; i <= 8; i++) {
            if (isValidPos(row, i)) {
                addMoves(row, i);
                if (isEnemy(row, i)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = col - 1; i >= 1; i--) {
            if (isValidPos(row, i)) {
                addMoves(row, i);
                if (isEnemy(row, i)) {
                    break;
                }
            } else {
                break;
            }
        }

        return moves;
    }
}