import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Abstract base class for all AST nodes
abstract class Node {
    public abstract String toString();
}

abstract class StatementNode extends Node {
}

class VariableNode extends Node {
    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

class StringNode extends Node {
    private String value;

    public StringNode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}

class ReadNode extends StatementNode {
    private List<VariableNode> variables;

    public ReadNode(List<VariableNode> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "read " + variables.stream().map(VariableNode::toString).collect(Collectors.joining(", "));
    }
}

class DataNode extends StatementNode {
    private List<Node> data;

    public DataNode(List<Node> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "data " + data.stream().map(Node::toString).collect(Collectors.joining(", "));
    }
}

class InputNode extends StatementNode {
    private Node prompt; // Can be StringNode or VariableNode
    private List<VariableNode> variables;

    public InputNode(Node prompt, List<VariableNode> variables) {
        this.prompt = prompt;
        this.variables = variables;
    }

    @Override
    public String toString() {
        String promptStr = prompt != null ? prompt.toString() + ", " : "";
        return "input " + promptStr + variables.stream().map(VariableNode::toString).collect(Collectors.joining(", "));
    }
}


class PrintNode extends StatementNode {
    private final List<Node> nodes;

    public PrintNode(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "print " + nodes.stream().map(Node::toString).collect(Collectors.joining(", "));
    }
}

class AssignmentNode extends StatementNode {
    private final VariableNode variable;
    private final Node value;

    public AssignmentNode(VariableNode variable, Node value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    public String toString() {
        return variable.toString() + " = " + value.toString();
    }
}

class StatementsNode extends Node {
    private final List<StatementNode> statements;

    public StatementsNode(List<StatementNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return statements.stream().map(StatementNode::toString).collect(Collectors.joining("\n"));
    }
}


// Node representing integer literals
class IntegerNode extends Node {
    private int value;

    public IntegerNode(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}

// Node representing floating point literals
class FloatNode extends Node {
    private float value;

    public FloatNode(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}

// Math operation node
class MathOpNode extends Node {
    enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    private Operation operation;
    private Node left;
    private Node right;

    public MathOpNode(Operation operation, Node left, Node right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + operation.name() + " " + right.toString() + ")";
    }
}

// Manages the stream of tokens
class TokenManager {
    private LinkedList<Token> tokens;

    public TokenManager(LinkedList<Token> tokens) {
        this.tokens = tokens;
    }

    public Optional<Token> peek(int j) {
        if (j >= 0 && j < tokens.size()) {
            return Optional.of(tokens.get(j));
        }
        return Optional.empty();
    }

    public boolean hasMoreTokens() {
        return !tokens.isEmpty();
    }

    public Optional<Token> matchAndRemove(Token.TokenType type) {
        if (!tokens.isEmpty() && tokens.getFirst().type == type) {
            return Optional.of(tokens.removeFirst());
        }
        return Optional.empty();
    }
}

class Parser {
    private LinkedList<Token> tokens;
    private Token currentToken;

    public Parser(LinkedList<Token> tokens) {
        this.tokens = tokens;
        // Initialize currentToken
        this.currentToken = tokens.isEmpty() ? null : tokens.pop();
    }

    private void eat(Token.TokenType type) {
        if (currentToken != null && currentToken.type == type) {
            currentToken = tokens.isEmpty() ? null : tokens.pop();
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken + ", expected: " + type);
        }
    }

    private Node parseExpression() {
        Node node = parseTerm();

        while (currentToken != null && (currentToken.type == Token.TokenType.PLUS || currentToken.type == Token.TokenType.MINUS)) {
            Token token = currentToken;
            if (token.type == Token.TokenType.PLUS) {
                eat(Token.TokenType.PLUS);
            } else if (token.type == Token.TokenType.MINUS) {
                eat(Token.TokenType.MINUS);
            }

            node = new MathOpNode(token.type == Token.TokenType.PLUS ? MathOpNode.Operation.ADD : MathOpNode.Operation.SUBTRACT, node, parseTerm());
        }

        return node;
    }

    private Node parseTerm() {
        Node node = parseFactor();

        while (currentToken != null && (currentToken.type == Token.TokenType.STAR || currentToken.type == Token.TokenType.SLASH)) {
            Token token = currentToken;
            if (token.type == Token.TokenType.STAR) {
                eat(Token.TokenType.STAR);
            } else if (token.type == Token.TokenType.SLASH) {
                eat(Token.TokenType.SLASH);
            }

            node = new MathOpNode(token.type == Token.TokenType.STAR ? MathOpNode.Operation.MULTIPLY : MathOpNode.Operation.DIVIDE, node, parseFactor());
        }

        return node;
    }

    private Node parseFactor() {
        Token token = currentToken;
        if (token.type == Token.TokenType.MINUS) {
            eat(Token.TokenType.MINUS);
            Node node = parseFactor(); // Handle unary minus
            return new MathOpNode(MathOpNode.Operation.SUBTRACT, new IntegerNode(0), node);
        } else if (token.type == Token.TokenType.NUMBER) {
            eat(Token.TokenType.NUMBER);
            return new IntegerNode(Integer.parseInt(token.value));
        } else if (token.type == Token.TokenType.STRINGLITERAL) {
            eat(Token.TokenType.STRINGLITERAL);
            return new StringNode(token.value); // Correctly handle STRINGLITERAL tokens
        } else if (token.type == Token.TokenType.LPAREN) {
            eat(Token.TokenType.LPAREN);
            Node node = parseExpression();
            eat(Token.TokenType.RPAREN);
            return node;
        } else if (token.type == Token.TokenType.WORD) {
            eat(Token.TokenType.WORD);
            return new VariableNode(token.value);
        }
        throw new RuntimeException("Unexpected token: " + token + " at line " + token + ", position " + token);
    }
    
    

    private StatementsNode Statements() {
    List<StatementNode> statementList = new ArrayList<>();
    while (true) {
        StatementNode statement = Statement();
        if (statement == null) {
            break;
        }
        statementList.add(statement);
    }
    return new StatementsNode(statementList);
}
private StatementNode Statement() {
    if (currentToken != null && currentToken.type == Token.TokenType.PRINT) {
        return PrintStatement();
    } else if (currentToken != null && currentToken.type == Token.TokenType.WORD) {
        return Assignment();
    }
    return null;
}

private PrintNode PrintStatement() {
    eat(Token.TokenType.PRINT);
    List<Node> printItems = new ArrayList<>();
    printItems.add(parseExpression()); // For simplicity, we start with a single item

    while (currentToken != null && currentToken.type == Token.TokenType.COMMA) {
        eat(Token.TokenType.COMMA);
        printItems.add(parseExpression());
    }

    return new PrintNode(printItems);
}

private AssignmentNode Assignment() {
    String varName = currentToken.value;
    eat(Token.TokenType.WORD);
    eat(Token.TokenType.EQUALS);
    Node value = parseExpression();
    return new AssignmentNode(new VariableNode(varName), value);
}


    public Node parse() {
        return Statements();
    }
    
}

