package chess.PieceMoves;

import chess.*;

import java.util.HashSet;

public abstract class PieceMove {

    protected ChessBoard board;
    protected ChessPosition myPosition;
    protected ChessPiece.PieceType myType;
    protected ChessGame.TeamColor myColor;
    protected HashSet<ChessMove> moves = new HashSet<>();

    public PieceMove(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        this.myType = piece.getPieceType();
        this.myColor = piece.getTeamColor();
        this.board = board;
        this.myPosition = myPosition;
    }

    public abstract HashSet<ChessMove> pieceMove(ChessBoard board, ChessPosition myPosition);

    public boolean isValidPos(int row, int col) {
        return (board.getPiece(new ChessPosition(row, col)) == null || board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor);
    }

    public boolean isEnemy(int row, int col) {
        return (board.getPiece(new ChessPosition(row, col)) != null && board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor);
    }

    public boolean isEmpty(int row, int col) {
        return board.getPiece(new ChessPosition(row, col)) == null;
    }
}
