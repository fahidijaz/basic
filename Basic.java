import java.io.IOException;
import java.util.LinkedList;

public class Basic {
    // The main method
    public static void main(String[] args) {
        // Check if exactly one argument (filename) is provided
        if (args.length != 1) {
            System.err.println("Error: Please provide exactly one filename.");
            System.exit(1);
        }

        try {
            Lexer lexer = new Lexer();
            LinkedList<Token> tokens = lexer.lex(args[0]);

            for (Token token : tokens) {
                System.out.println(token);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (LexerException e) {
            System.err.println("Lexer error: " + e.getMessage());
        }
    }
}
