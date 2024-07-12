package chess.PieceMoves;

import chess.*;

import java.util.HashSet;

public class KingMove extends PieceMove {

    public KingMove(ChessPiece piece, ChessBoard board, ChessPosition myPos) {
        super(piece, board, myPos);
    }

    @Override
    public HashSet<ChessMove> pieceMove() {
        int row = myPos.getRow();
        int col = myPos.getColumn();

        if (row + 1 <= 8 && isValidPos(row + 1, col)) {
            addMoves(row + 1, col);
        }
        if (row - 1 >= 1 && isValidPos(row - 1, col)) {
            addMoves(row - 1, col);
        }
        if (col + 1 <= 8 && isValidPos(row, col + 1)) {
            addMoves(row, col + 1);
        }
        if (col - 1 >= 1 && isValidPos(row, col - 1)) {
            addMoves(row, col - 1);
        }
        if (row + 1 <= 8 && col + 1 <= 8 && isValidPos(row + 1, col + 1)) {
            addMoves(row + 1, col + 1);
        }
        if (row + 1 <= 8 && col - 1 >= 1 && isValidPos(row + 1, col - 1)) {
            addMoves(row + 1, col - 1);
        }
        if (row - 1 >= 1 && col + 1 <= 8 && isValidPos(row - 1, col + 1)) {
            addMoves(row - 1, col + 1);
        }
        if (row - 1 >= 1 && col - 1 >= 1 && isValidPos(row - 1, col - 1)) {
            addMoves(row - 1, col - 1);
        }

        if (previousMove == null) {
            if (row == 1 || row == 8) {
                if (board.getPiece(new ChessPosition(row, 2)) == null &&
                        board.getPiece(new ChessPosition(row, 3)) == null &&
                        board.getPiece(new ChessPosition(row, 4)) == null) {
                    ChessPiece rook = board.getPiece(new ChessPosition(row, 1));
                    if (rook != null && rook.getPreviousMove() == null) {
                        addMoves(row, 3);
                    }
                }
                if (board.getPiece(new ChessPosition(row, 6)) == null &&
                        board.getPiece(new ChessPosition(row, 7)) == null) {
                    ChessPiece rook = board.getPiece(new ChessPosition(row, 8));
                    if (rook != null && rook.getPreviousMove() == null) {
                        addMoves(row, 7);
                    }
                }
            }
        }

        return moves;
    }

}
