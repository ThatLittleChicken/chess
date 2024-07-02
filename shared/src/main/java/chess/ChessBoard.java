package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        new ChessBoard();
        for (int i = 0; i < 2; i++) {
            int row = i * 7;
            ChessGame.TeamColor color = i == 0 ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            board[row][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            board[row][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            board[row][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            board[row][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
            board[row][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
            board[row][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            board[row][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            board[row][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        }
        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
