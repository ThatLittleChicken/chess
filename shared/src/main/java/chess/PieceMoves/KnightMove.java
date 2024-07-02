package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMove extends PieceMove {
    public KnightMove(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        super(piece, board, myPosition);
    }

    @Override
    public HashSet<ChessMove> pieceMove(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        //knight moves if the position is empty
        //up left
        if (row + 2 <= 8 && col - 1 >= 1 && isValidPos(row + 2, col - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col - 1), null));
        }
        //up right
        if (row + 2 <= 8 && col + 1 <= 8 && isValidPos(row + 2, col + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col + 1), null));
        }
        //up mid left
        if (row + 1 <= 8 && col - 2 >= 1 && isValidPos(row + 1, col - 2)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col - 2), null));
        }
        //up mid right
        if (row + 1 <= 8 && col + 2 <= 8 && isValidPos(row + 1, col + 2)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col + 2), null));
        }
        //down mid left
        if (row - 1 >= 1 && col - 2 >= 1 && isValidPos(row - 1, col - 2)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col - 2), null));
        }
        //down mid right
        if (row - 1 >= 1 && col + 2 <= 8 && isValidPos(row - 1, col + 2)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col + 2), null));
        }
        //down left
        if (row - 2 >= 1 && col - 1 >= 1 && isValidPos(row - 2, col - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col - 1), null));
        }
        //down right
        if (row - 2 >= 1 && col + 1 <= 8 && isValidPos(row - 2, col + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col + 1), null));
        }

        return moves;
    }
}
