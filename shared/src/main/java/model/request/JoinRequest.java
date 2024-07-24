package model.request;

public record JoinRequest(String authToken, int gameID, String playerColor) {
}
