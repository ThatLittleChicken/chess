import ui.Repl;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1) {
            Repl repl = new Repl(Integer.parseInt(args[0]));
            repl.run();
        }
        Repl repl = new Repl(8080);
        repl.run();
    }
}