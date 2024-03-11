import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Additional imports
import java.io.IOException;
import java.util.LinkedList;

public class ParserTest {

    // Helper method for parsing expressions directly from a string just for testing
    private Node parseExpression(String expression) throws IOException, LexerException {
        Lexer lexer = new Lexer();
        // Assuming lexString is implemented to handle direct string input
        LinkedList<Token> tokens = lexer.lexString(expression); 
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    @Test
    public void testVariableAssignment() throws IOException, LexerException {
        Node ast = parseExpression("x = 10");
        assertEquals("x = 10", ast.toString());
    }

    @Test
    public void testPrintStatement() throws IOException, LexerException {
        Node ast = parseExpression("print \"Hello, World!\"");
        assertEquals("print \"Hello, World!\"", ast.toString());
    }

    @Test
    public void testMultipleStatements() throws IOException, LexerException {
        Node ast = parseExpression("x = 10; print x; y = x + 5; print y");
        assertEquals("x = 10\nprint x\ny = x + 5\nprint y", ast.toString());
    }

    @Test
    public void testReadStatement() throws IOException, LexerException {
        Node ast = parseExpression("READ x, y");
        assertEquals("read x, y", ast.toString());
    }

    @Test
    public void testDataStatement() throws IOException, LexerException {
        Node ast = parseExpression("DATA \"Sample String\", 123, 45.67");
        assertEquals("data \"Sample String\", 123, 45.67", ast.toString());
    }

    @Test
    public void testInputStatement() throws IOException, LexerException {
        Node ast = parseExpression("INPUT \"Enter value: \", z");
        assertEquals("input \"Enter value: \", z", ast.toString());
    }

    @Test
    public void testMathOperation() throws IOException, LexerException {
        Node ast = parseExpression("z = x + y");
        assertEquals("z = (x ADD y)", ast.toString());
    }
}
