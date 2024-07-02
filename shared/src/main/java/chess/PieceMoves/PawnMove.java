package chess.PieceMoves;

import chess.*;

import java.util.HashSet;

public class PawnMove extends PieceMove {
    public PawnMove(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        super(piece, board, myPosition);
    }

    private void promotionMoves(int row, int col) {
        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.KNIGHT));
    }

    @Override
    public HashSet<ChessMove> pieceMove(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        //pawn moves if the position is empty
        if (myColor == ChessGame.TeamColor.WHITE) {
            //white
            //up
            if (row + 1 <= 8 && isEmpty(row + 1, col)) {
                if (row + 1 == 8) {
                    //promotion
                    promotionMoves(row + 1, col);
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
                }
            }
            //up two
            if (row == 2 && isEmpty(row + 1, col) && isEmpty(row + 2, col)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col), null));
            }
            //up left
            if (row + 1 <= 8 && col - 1 >= 1 && isEnemy(row + 1, col - 1)) {
                if (row + 1 == 8) {
                    //promotion
                    promotionMoves(row + 1, col - 1);
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col - 1), null));
                }
            }
            //up right
            if (row + 1 <= 8 && col + 1 <= 8 && isEnemy(row + 1, col + 1)) {
                if (row + 1 == 8) {
                    //promotion
                    promotionMoves(row + 1, col + 1);
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col + 1), null));
                }
            }
        } else {
            //black
            //down
            if (row - 1 >= 1 && isEmpty(row - 1, col)) {
                if (row - 1 == 1) {
                    //promotion
                    promotionMoves(row - 1, col);
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
                }
            }
            //down two
            if (row == 7 && isEmpty(row - 1, col) && isEmpty(row - 2, col)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col), null));
            }
            //down left
            if (row - 1 >= 1 && col - 1 >= 1 && isEnemy(row - 1, col - 1)) {
                if (row - 1 == 1) {
                    //promotion
                    promotionMoves(row - 1, col - 1);
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col - 1), null));
                }
            }
            //down right
            if (row - 1 >= 1 && col + 1 <= 8 && isEnemy(row - 1, col + 1)) {
                if (row - 1 == 1) {
                    //promotion
                    promotionMoves(row - 1, col + 1);
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col + 1), null));
                }
            }
        }

        return moves;
    }
}
