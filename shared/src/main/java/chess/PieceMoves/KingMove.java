package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashSet;

public class KingMove extends PieceMove {
    public KingMove(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        super(piece, board, myPosition);
    }

    @Override
    public HashSet<ChessMove> pieceMove(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        //king moves if the position is empty
        //up
        if (row + 1 <= 8 && isValidPos(row + 1, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
        }
        //down
        if (row - 1 >= 1 && isValidPos(row - 1, col)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
        }
        //left
        if (col - 1 >= 1 && isValidPos(row, col - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col - 1), null));
        }
        //right
        if (col + 1 <= 8 && isValidPos(row, col + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col + 1), null));
        }
        //up left
        if (row + 1 <= 8 && col - 1 >= 1 && isValidPos(row + 1, col - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col - 1), null));
        }
        //up right
        if (row + 1 <= 8 && col + 1 <= 8 && isValidPos(row + 1, col + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col + 1), null));
        }
        //down left
        if (row - 1 >= 1 && col - 1 >= 1 && isValidPos(row - 1, col - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col - 1), null));
        }
        //down right
        if (row - 1 >= 1 && col + 1 <= 8 && isValidPos(row - 1, col + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col + 1), null));
        }

        return moves;
    }
}
