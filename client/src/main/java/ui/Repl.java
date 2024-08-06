package ui;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;
    private final BoardUI boardUI = new BoardUI();

    public Repl(int port) {
        client = new ChessClient(new ServerFacade(port));
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
                printBoard();
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

    public void printBoard() {
        if (client.getCurrentGame() != null) {
            System.out.println();
            System.out.println(boardUI.drawBoard(client.getCurrentGame()));
        }
    }
}
