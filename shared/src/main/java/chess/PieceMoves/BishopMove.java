package chess.piecemoves;

import chess.*;

import java.util.HashSet;

public class BishopMove extends PieceMove {

    public BishopMove(ChessPiece piece, ChessBoard board, ChessPosition myPos) {
        super(piece, board, myPos);
    }

    @Override
    public HashSet<ChessMove> pieceMove() {
        int row = myPos.getRow();
        int col = myPos.getColumn();

        return diagonalMoves(row, col, moves);
    }
}
