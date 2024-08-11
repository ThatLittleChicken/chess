package ui;

import ui.websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(int port) {
        client = new ChessClient(port, this);
    }

    public void run() {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println("♕ Welcome to 240 chess. ♕");
        System.out.println("Type 'help' for a list of commands or 'exit' to quit.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + "[" + client.getState() + "] >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    public void notify(NotificationMessage notificationMessage) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + notificationMessage.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
    }

    public void notify(ErrorMessage errorMessage) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + errorMessage.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
    }

    public void notify(LoadGameMessage loadGameMessage) {
        System.out.println(client.redrawBoard());
    }

}
