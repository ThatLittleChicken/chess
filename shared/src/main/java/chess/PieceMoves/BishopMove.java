package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashSet;

public class BishopMove extends PieceMove {
    public BishopMove(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        super(piece, board, myPosition);
    }

    @Override
    public HashSet<ChessMove> pieceMove(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        //bishop moves if the position is empty
        //up left
        for (int i = 1; row + i <= 8 && col - i >= 1; i++) {
            if (isValidPos(row + i, col - i)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + i, col - i), null));
                if (isEnemy(row + i, col - i)) {
                    break;
                }
            } else {
                break;
            }
        }
        //up right
        for (int i = 1; row + i <= 8 && col + i <= 8; i++) {
            if (isValidPos(row + i, col + i)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + i, col + i), null));
                if (isEnemy(row + i, col + i)) {
                    break;
                }
            } else {
                break;
            }
        }
        //down left
        for (int i = 1; row - i >= 1 && col - i >= 1; i++) {
            if (isValidPos(row - i, col - i)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row - i, col - i), null));
                if (isEnemy(row - i, col - i)) {
                    break;
                }
            } else {
                break;
            }
        }
        //down right
        for (int i = 1; row - i >= 1 && col + i <= 8; i++) {
            if (isValidPos(row - i, col + i)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row - i, col + i), null));
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
