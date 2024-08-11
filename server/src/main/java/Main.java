import server.Server;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1) {
            new Server().run(Integer.parseInt(args[0]));
        } else {
            new Server().run(8080);
        }
    }
}