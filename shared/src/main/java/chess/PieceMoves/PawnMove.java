package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class PawnMove extends PieceMove {
    public PawnMove(ChessGame.TeamColor myColor, ChessBoard board, ChessPosition myPos) {
        super(myColor, board, myPos);
    }

    @Override
    public HashSet<ChessMove> pieceMove() {
        int row = myPos.getRow();
        int col = myPos.getColumn();

        if (myColor == ChessGame.TeamColor.WHITE){
            if (row + 1 <= 8 && isEmpty(row + 1, col)) {
                if (row + 1 == 8) {
                    addPromotionMoves(row + 1, col);
                } else {
                    addMoves(row + 1, col);
                }
            }
            if (row == 2 && isEmpty(row + 1, col) && isEmpty(row + 2, col)) {
                addMoves(row + 2, col);
            }
            if (row + 1 <= 8 && col + 1 <= 8 && isEnemy(row + 1, col + 1)) {
                if (row + 1 == 8) {
                    addPromotionMoves(row + 1, col + 1);
                } else {
                    addMoves(row + 1, col + 1);
                }
            }
            if (row + 1 <= 8 && col - 1 >= 1 && isEnemy(row + 1, col - 1)) {
                if (row + 1 == 8) {
                    addPromotionMoves(row + 1, col - 1);
                } else {
                    addMoves(row + 1, col - 1);
                }
            }
        } else {
            if (row - 1 >= 1 && isEmpty(row - 1, col)) {
                if (row - 1 == 1) {
                    addPromotionMoves(row - 1, col);
                } else {
                    addMoves(row - 1, col);
                }
            }
            if (row == 7 && isEmpty(row - 1, col) && isEmpty(row - 2, col)) {
                addMoves(row - 2, col);
            }
            if (row - 1 >= 1 && col - 1 >= 1 && isEnemy(row - 1, col - 1)) {
                if (row - 1 == 1) {
                    addPromotionMoves(row - 1, col - 1);
                } else {
                    addMoves(row - 1, col - 1);
                }
            }
            if (row - 1 >= 1 && col + 1 <= 8 && isEnemy(row - 1, col + 1)) {
                if (row - 1 == 1) {
                    addPromotionMoves(row - 1, col + 1);
                } else {
                    addMoves(row - 1, col + 1);
                }
            }
        }

        return moves;
    }
}
