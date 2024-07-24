package chess.piecemoves;

import chess.*;

import java.util.HashSet;

public class QueenMove extends PieceMove {

    public QueenMove(ChessPiece piece, ChessBoard board, ChessPosition myPos) {
        super(piece, board, myPos);
    }

    @Override
    public HashSet<ChessMove> pieceMove() {
        int row = myPos.getRow();
        int col = myPos.getColumn();

        moves.addAll(diagonalMoves(row, col, moves));
        moves.addAll(horizontalMoves(row, col, moves));

        return moves;
    }
}
