package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        } else {
            return validMoves(startPosition, board.getPiece(startPosition).getTeamColor());
        }
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition, TeamColor teamColor) {
        if (board.getPiece(startPosition) != null && board.getPiece(startPosition).getTeamColor() == teamColor) {
            Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
            Collection<ChessMove> validMoves = new HashSet<>();
            for (ChessMove move : moves) {
                ChessPiece piece = board.getPiece(startPosition);
                ChessPiece promotionPiece = board.getPiece(startPosition);
                ChessPiece temp = board.getPiece(move.getEndPosition());
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (move.getStartPosition().getColumn() == 5 && move.getEndPosition().getColumn() == 3) {
                        if (isThreatened(new ChessPosition(startPosition.getRow(), 5), teamColor) ||
                                isThreatened(new ChessPosition(startPosition.getRow(), 4), teamColor) ||
                                isThreatened(new ChessPosition(startPosition.getRow(), 3), teamColor)) {
                            continue;
                        }
                    } else if (move.getStartPosition().getColumn() == 5 && move.getEndPosition().getColumn() == 7) {
                        if (isThreatened(new ChessPosition(startPosition.getRow(), 5), teamColor) ||
                                isThreatened(new ChessPosition(startPosition.getRow(), 6), teamColor) ||
                                isThreatened(new ChessPosition(startPosition.getRow(), 7), teamColor)) {
                            continue;
                        }
                    }
                }
                if (move.getPromotionPiece() != null) {
                    promotionPiece = new ChessPiece(teamColor, move.getPromotionPiece());
                }
                board.addPiece(move.getEndPosition(), promotionPiece);
                board.addPiece(startPosition, null);
                if (!isInCheck(teamColor)) {
                    validMoves.add(move);
                }
                board.addPiece(startPosition, piece);
                board.addPiece(move.getEndPosition(), temp);
            }
            return validMoves;
        } else {
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (validMoves(move.getStartPosition()) == null || !validMoves(move.getStartPosition()).contains(move) ||
                board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Invalid move");
        } else {
            if (move.getStartPosition().getColumn() == 5 && move.getEndPosition().getColumn() == 3) {
                ChessPiece rook = board.getPiece(new ChessPosition(move.getStartPosition().getRow(), 1));
                board.addPiece(new ChessPosition(move.getStartPosition().getRow(), 4), rook);
                board.addPiece(new ChessPosition(move.getStartPosition().getRow(), 1), null);
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                board.addPiece(move.getStartPosition(), null);
            } else if (move.getStartPosition().getColumn() == 5 && move.getEndPosition().getColumn() == 7) {
                ChessPiece rook = board.getPiece(new ChessPosition(move.getStartPosition().getRow(), 8));
                board.addPiece(new ChessPosition(move.getStartPosition().getRow(), 6), rook);
                board.addPiece(new ChessPosition(move.getStartPosition().getRow(), 8), null);
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                board.addPiece(move.getStartPosition(), null);
            } else if (move.getPromotionPiece() != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(getTeamTurn(), move.getPromotionPiece()));
                board.addPiece(move.getStartPosition(), null);
            } else {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                board.addPiece(move.getStartPosition(), null);
            }
            board.getPiece(move.getEndPosition()).setPreviousMove(move);
        }

        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    public boolean isThreatened(ChessPosition position, TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, new ChessPosition(i, j));
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return isThreatened(new ChessPosition(i, j), teamColor);
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    if (validMoves(new ChessPosition(i, j), teamColor) != null &&
                            !validMoves(new ChessPosition(i, j), teamColor).isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    if (validMoves(new ChessPosition(i, j), teamColor) != null &&
                            !validMoves(new ChessPosition(i, j), teamColor).isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
