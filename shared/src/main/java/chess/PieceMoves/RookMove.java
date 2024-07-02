package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMove extends PieceMove {
    public RookMove(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        super(piece, board, myPosition);
    }

    @Override
    public HashSet<ChessMove> pieceMove(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        //rook moves if the position is empty
        //up
        for (int i = row + 1; i <= 8; i++) {
            if (isValidPos(i, col)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, col), null));
                if (isEnemy(i, col)) {
                    break;
                }
            } else {
                break;
            }
        }
        //down
        for (int i = row - 1; i >= 1; i--) {
            if (isValidPos(i, col)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, col), null));
                if (isEnemy(i, col)) {
                    break;
                }
            } else {
                break;
            }
        }
        //left
        for (int i = col - 1; i >= 1; i--) {
            if (isValidPos(row, i)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, i), null));
                if (isEnemy(row, i)) {
                    break;
                }
            } else {
                break;
            }
        }
        //right
        for (int i = col + 1; i <= 8; i++) {
            if (isValidPos(row, i)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, i), null));
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
