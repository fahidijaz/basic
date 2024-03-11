import java.util.HashMap;
import java.util.LinkedList;
import java.io.IOException;

public class Lexer {
    private HashMap<String, Token.TokenType> knownWords;
    private HashMap<String, Token.TokenType> twoCharSymbols;
    private HashMap<String, Token.TokenType> oneCharSymbols;
    private int lineNumber = 1;
    private int charPosition = 1;

    // Constructor to initialize known words and symbols
    public Lexer() {
        initializeKnownWords();
        initializeSymbols();
    }

    private void initializeKnownWords() {
        knownWords = new HashMap<>();
        // Add BASIC keywords
        knownWords.put("print", Token.TokenType.PRINT);
        knownWords.put("read", Token.TokenType.READ);
        knownWords.put("input", Token.TokenType.INPUT);
        knownWords.put("data", Token.TokenType.DATA);
        knownWords.put("gosub", Token.TokenType.GOSUB);
        knownWords.put("for", Token.TokenType.FOR);
        knownWords.put("to", Token.TokenType.TO);
        knownWords.put("step", Token.TokenType.STEP);
        knownWords.put("next", Token.TokenType.NEXT);
        knownWords.put("return", Token.TokenType.RETURN);
        knownWords.put("if", Token.TokenType.IF);
        knownWords.put("then", Token.TokenType.THEN);
        knownWords.put("function", Token.TokenType.FUNCTION);
        knownWords.put("while", Token.TokenType.WHILE);
        knownWords.put("end", Token.TokenType.END);
        // Add other BASIC keywords as needed
    }

    private void initializeSymbols() {
        twoCharSymbols = new HashMap<>();
        // Two-character symbols
        twoCharSymbols.put("<=", Token.TokenType.LEQ);
        twoCharSymbols.put(">=", Token.TokenType.GEQ);
        twoCharSymbols.put("<>", Token.TokenType.NOTEQUALS);
        // Add other two-character symbols as needed

        oneCharSymbols = new HashMap<>();
        // One-character symbols
        oneCharSymbols.put("=", Token.TokenType.EQUALS);
        oneCharSymbols.put("<", Token.TokenType.LESSTHAN);
        oneCharSymbols.put(">", Token.TokenType.GREATERTHAN);
        oneCharSymbols.put("+", Token.TokenType.PLUS);
        oneCharSymbols.put("-", Token.TokenType.MINUS);
        oneCharSymbols.put("*", Token.TokenType.STAR);
        oneCharSymbols.put("/", Token.TokenType.SLASH);
        oneCharSymbols.put("(", Token.TokenType.LPAREN);
        oneCharSymbols.put(")", Token.TokenType.RPAREN);
        oneCharSymbols.put(";", Token.TokenType.RPAREN);
        oneCharSymbols.put("%", Token.TokenType.RPAREN);
        oneCharSymbols.put(",", Token.TokenType.COMMA);
        
    }

    // The main lexer function that tokenizes the input code
    public LinkedList<Token> lex(String filename) throws IOException, LexerException {
        LinkedList<Token> tokens = new LinkedList<>();
        CodeHandler codeHandler = new CodeHandler(filename);

        while (!codeHandler.isDone()) {
            char ch = codeHandler.peek(0);

            if (Character.isWhitespace(ch)) {
                handleWhitespace(codeHandler);
            } else if (Character.isDigit(ch)) {
                tokens.add(processNumber(codeHandler));
            } else if (Character.isLetter(ch) || ch == '_') {
                tokens.add(processWord(codeHandler));
            } else if (ch == '"') {
                tokens.add(processStringLiteral(codeHandler));
            } else if (isSymbol(ch, codeHandler)) {
                tokens.add(processSymbol(codeHandler));
            } else {
                throw new LexerException("Unrecognized character: " + ch);
            }
        }

        return tokens;
    }

    // Handles whitespace characters, updates line number and char position
    private void handleWhitespace(CodeHandler codeHandler) {
        char ch = codeHandler.getChar();
        if (ch == '\n') {
            lineNumber++;
            charPosition = 1;
        } else {
            charPosition++;
        }
    }

    // Processes a word and returns a Token
    private Token processWord(CodeHandler codeHandler) {
        StringBuilder word = new StringBuilder();
        while (Character.isLetterOrDigit(codeHandler.peek(0)) || codeHandler.peek(0) == '_') {
            word.append(codeHandler.getChar());
            charPosition++;
        }

        String wordStr = word.toString().toLowerCase();
        if (knownWords.containsKey(wordStr)) {
            return new Token(knownWords.get(wordStr), lineNumber, charPosition - word.length());
        } else {
            return new Token(Token.TokenType.WORD, lineNumber, charPosition - word.length(), wordStr);
        }
    }

    // Processes a number and returns a Token
    private Token processNumber(CodeHandler codeHandler) {
        StringBuilder number = new StringBuilder();
        while (Character.isDigit(codeHandler.peek(0)) || codeHandler.peek(0) == '.') {
            number.append(codeHandler.getChar());
            charPosition++;
        }
        return new Token(Token.TokenType.NUMBER, lineNumber, charPosition - number.length(), number.toString());
    }

    // Processes a string and returns a Token
    private Token processStringLiteral(CodeHandler codeHandler) throws LexerException {
        StringBuilder strLiteral = new StringBuilder();
        codeHandler.getChar(); // Consume opening quote
        charPosition++;

        while (codeHandler.peek(0) != '"' && !codeHandler.isDone()) {
            if (codeHandler.peek(0) == '\\' && codeHandler.peek(1) == '"') {
                codeHandler.getChar(); // Skip escape character
                charPosition++;
            }
            strLiteral.append(codeHandler.getChar());
            charPosition++;
        }

        if (codeHandler.peek(0) == '"') {
            codeHandler.getChar(); // Consume closing quote
            charPosition++;
        } else {
            throw new LexerException("Unmatched quote in string literal");
        }

        return new Token(Token.TokenType.STRINGLITERAL, lineNumber, charPosition - strLiteral.length() - 2, strLiteral.toString());
    }

    // Checks if the character is a symbol
    private boolean isSymbol(char ch, CodeHandler codeHandler) {
        return oneCharSymbols.containsKey(String.valueOf(ch)) ||
               twoCharSymbols.containsKey(String.valueOf(ch) + codeHandler.peek(1));
    }

    // Processes a symbol and returns a Token
    private Token processSymbol(CodeHandler codeHandler) {
        char firstChar = codeHandler.getChar();
        String symbol = String.valueOf(firstChar);
        Token.TokenType type = oneCharSymbols.getOrDefault(symbol, Token.TokenType.SPECIAL_CHAR);

        // Check for two-character symbol
        if (twoCharSymbols.containsKey(symbol + codeHandler.peek(0))) {
            symbol += codeHandler.getChar();
            type = twoCharSymbols.get(symbol);
            charPosition += 2;
        } else {
            charPosition++;
        }

        return new Token(type, lineNumber, charPosition - symbol.length(), symbol);
    }
}

class LexerException extends Exception {
    public LexerException(String message) {
        super(message);
    }

}
