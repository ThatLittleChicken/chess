package chess.piecemoves;

import chess.*;

import java.util.HashSet;

public abstract class PieceMove {

    protected ChessBoard board;
    protected ChessGame.TeamColor myColor;
    protected ChessPosition myPos;
    protected ChessMove previousMove;
    protected HashSet<ChessMove> moves;

    public PieceMove(ChessPiece piece, ChessBoard board, ChessPosition myPos) {
        this.board = board;
        this.myColor = piece.getTeamColor();
        this.myPos = myPos;
        this.previousMove = piece.getPreviousMove();
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

    public HashSet<ChessMove> diagonalMoves(int row, int col, HashSet<ChessMove> moves) {
        for (int i = 1; row + i <= 8 && col + i <= 8; i++) {
            if (isValidPos(row + i, col + i)) {
                addMoves(row + i, col + i);
                if (isEnemy(row + i, col + i)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; row - i >= 1 && col - i >= 1; i++) {
            if (isValidPos(row - i, col - i)) {
                addMoves(row - i, col - i);
                if (isEnemy(row - i, col - i)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; row + i <= 8 && col - i >= 1; i++) {
            if (isValidPos(row + i, col - i)) {
                addMoves(row + i, col - i);
                if (isEnemy(row + i, col - i)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; row - i >= 1 && col + i <= 8; i++) {
            if (isValidPos(row - i, col + i)) {
                addMoves(row - i, col + i);
                if (isEnemy(row - i, col + i)) {
                    break;
                }
            } else {
                break;
            }
        }

        return moves;
    }

    public HashSet<ChessMove> horizontalMoves(int row, int col, HashSet<ChessMove> moves) {
        for (int i = row + 1; i <= 8; i++) {
            if (isValidPos(i, col)) {
                addMoves(i, col);
                if (isEnemy(i, col)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = row - 1; i >= 1; i--) {
            if (isValidPos(i, col)) {
                addMoves(i, col);
                if (isEnemy(i, col)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = col + 1; i <= 8; i++) {
            if (isValidPos(row, i)) {
                addMoves(row, i);
                if (isEnemy(row, i)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = col - 1; i >= 1; i--) {
            if (isValidPos(row, i)) {
                addMoves(row, i);
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
