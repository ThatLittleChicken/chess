package chess.PieceMoves;

import chess.*;

import java.util.HashSet;

public abstract class PieceMove {

    protected ChessBoard board;
    protected ChessGame.TeamColor myColor;
    protected ChessPosition myPos;
    protected HashSet<ChessMove> moves;

    public PieceMove(ChessGame.TeamColor myColor, ChessBoard board, ChessPosition myPos) {
        this.board = board;
        this.myColor = myColor;
        this.myPos = myPos;
        moves = new HashSet<>();
    }

    public abstract HashSet<ChessMove> pieceMove();

    public boolean isValidPos(int row, int col) {
        return (board.getPiece(new ChessPosition(row, col)) == null || board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor);
    }

    public boolean isEnemy(int row, int col) {
        return (board.getPiece(new ChessPosition(row, col)) != null && board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor);
    }

    public boolean isEmpty(int row, int col) {
        return board.getPiece(new ChessPosition(row, col)) == null;
    }

    public void addMoves(int row, int col) {
        moves.add(new ChessMove(myPos, new ChessPosition(row, col), null));
    }

    public void addPromotionMoves(int row, int col) {
        moves.add(new ChessMove(myPos, new ChessPosition(row, col), ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPos, new ChessPosition(row, col), ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(myPos, new ChessPosition(row, col), ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPos, new ChessPosition(row, col), ChessPiece.PieceType.QUEEN));
    }
}
