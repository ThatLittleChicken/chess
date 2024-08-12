package ui;

import chess.ChessGame;
import chess.ChessPosition;
import model.GameData;

import java.util.HashSet;
import java.util.Set;

public class BoardUI {
    public BoardUI() {
    }

    public String drawBoard(ChessGame game, ChessGame.TeamColor color) {
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            return drawWhiteBoard(game, null);
        } else {
            return drawBlackBoard(game, null);
        }
    }

    public String drawHighlightedBoard(ChessGame game, ChessPosition position, ChessGame.TeamColor color) {
        Set<ChessPosition> validPos = new HashSet<>();
        game.validMoves(position).forEach(move -> {
            if (color == null) {
                validPos.add(move.getEndPosition());
            } else if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor().equals(color)) {
                validPos.add(move.getEndPosition());
            }
        });
        if (color == null || color.equals(ChessGame.TeamColor.WHITE)) {
            return drawWhiteBoard(game, validPos);
        } else {
            return drawBlackBoard(game, validPos);
        }
    }

    private String drawWhiteBoard(ChessGame game, Set<ChessPosition> validPos) {
        StringBuilder result = new StringBuilder();
        result.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        result.append(String.format("%s%s a  b  c  d  e  f  g  h %s%s\n",
                EscapeSequences.SET_BG_COLOR_LIGHT_BROWN, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.RESET_BG_COLOR));
        for (int i = 8; i >= 1; i--) {
            result.append(String.format("%s %s ", EscapeSequences.SET_BG_COLOR_LIGHT_BROWN, i));
            for (int j = 1; j <= 8; j++) {
                String bgSquare;
                if (validPos != null && validPos.contains(new ChessPosition(i, j))) {
                    if ((i+j) % 2 == 0) {
                        bgSquare = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                    } else {
                        bgSquare = EscapeSequences.SET_BG_COLOR_GREEN;
                    }
                } else if ((i+j) % 2 == 0) {
                    bgSquare = EscapeSequences.SET_BG_COLOR_BROWN;
                } else {
                    bgSquare = EscapeSequences.SET_BG_COLOR_BEIGE;
                }
                result.append(String.format("%s%s", bgSquare, pieceSymbol(game, i, j)));
            }
            result.append(String.format("%s %s %s\n", EscapeSequences.SET_BG_COLOR_LIGHT_BROWN, i, EscapeSequences.RESET_BG_COLOR));
        }
        result.append(String.format("%s%s a  b  c  d  e  f  g  h %s%s",
                EscapeSequences.SET_BG_COLOR_LIGHT_BROWN, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.RESET_BG_COLOR));
        return result.toString();
    }

    public String drawBlackBoard(ChessGame game, Set<ChessPosition> validPos) {
        StringBuilder result = new StringBuilder();
        result.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        result.append(String.format("%s%s h  g  f  e  d  c  b  a %s%s\n",
                EscapeSequences.SET_BG_COLOR_LIGHT_BROWN, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.RESET_BG_COLOR));
        for (int i = 1; i <= 8; i++) {
            result.append(String.format("%s %s ", EscapeSequences.SET_BG_COLOR_LIGHT_BROWN, i));
            for (int j = 8; j >= 1; j--) {
                String squareBg;
                if (validPos != null && validPos.contains(new ChessPosition(i, j))) {
                    if ((i+j) % 2 != 0) {
                        squareBg = EscapeSequences.SET_BG_COLOR_GREEN;
                    } else {
                        squareBg = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                    }
                } else if ((i+j) % 2 != 0) {
                    squareBg = EscapeSequences.SET_BG_COLOR_BEIGE;
                } else {
                    squareBg = EscapeSequences.SET_BG_COLOR_BROWN;
                }
                result.append(String.format("%s%s", squareBg, pieceSymbol(game, i, j)));
            }
            result.append(String.format("%s %s %s\n", EscapeSequences.SET_BG_COLOR_LIGHT_BROWN, i, EscapeSequences.RESET_BG_COLOR));
        }
        result.append(String.format("%s%s h  g  f  e  d  c  b  a %s%s",
                EscapeSequences.SET_BG_COLOR_LIGHT_BROWN, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.RESET_BG_COLOR));
        return result.toString();
    }

    private String pieceSymbol(ChessGame game, int row, int col) {
        var chessPiece = game.getBoard().getPiece(new ChessPosition(row, col));
        if (chessPiece == null) {
            return EscapeSequences.EMPTY;
        }
        var pieceColor = chessPiece.getTeamColor();
        return switch (chessPiece.getPieceType()) {
            case KING -> pieceColor.equals(ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> pieceColor.equals(ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> pieceColor.equals(ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> pieceColor.equals(ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK -> pieceColor.equals(ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN -> pieceColor.equals(ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}
