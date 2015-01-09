package ws.chojnacki;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            for (String arg:args) {
                System.err.println(arg);
            }
        }
    }
}
