public class Token {
    // Enumeration for different types of tokens
    enum TokenType {
        WORD, NUMBER, ENDOFLINE, STRINGLITERAL, LABEL,
        PRINT, READ, INPUT, DATA, GOSUB, FOR, TO, STEP, NEXT, RETURN, IF, THEN, FUNCTION, WHILE, END,
        EQUALS, NOTEQUALS, LPAREN, RPAREN, PLUS, MINUS, STAR, SLASH, LESSTHAN, GREATERTHAN, LEQ, GEQ,
        SPECIAL_CHAR
    }

    private TokenType type;
    private String value;
    private int lineNumber;
    private int charPosition;

    // Constructor to create a token with a type, line number, position, and value
    public Token(TokenType type, int lineNumber, int charPosition, String value) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.charPosition = charPosition;
        this.value = value;
    }

    // Overloaded constructor for tokens
    public Token(TokenType type, int lineNumber, int charPosition) {
        this(type, lineNumber, charPosition, null);
    }

    // Override of the toString method for easy representation of the token
    @Override
    public String toString() {
        return type + (value != null ? "(" + value + ")" : "") +
               " at line " + lineNumber + ", position " + charPosition;
    }
}
