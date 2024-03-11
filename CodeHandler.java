import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class CodeHandler {
    private String document;
    private int index = 0;

    // Constructor to initialize the CodeHandler object
    public CodeHandler(String filename) throws IOException {
        this.document = new String(Files.readAllBytes(Paths.get(filename)));
    }

    // Method to get a substring from the current position
    public String peekString(int i) {
        if (index + i >= document.length()) return "";
        return document.substring(index, index + i);
    }

    // Method to move the index forward by a specified length
    public void swallow(int i) {
        index = Math.min(index + i, document.length());
    }

    // Method to get the remaining part of the document
    public String remainder() {
        if (isDone()) return "";
        return document.substring(index);
    }

    // Method to peek a single character at a specified position
    public char peek(int i) {
        if (index + i >= document.length()) return '\0';
        return document.charAt(index + i);
    }

    // Method to get the current character and move the index
    public char getChar() {
        if (index >= document.length()) return '\0';
        return document.charAt(index++);
    }

    // Method to check if the entire document has been processed
    public boolean isDone() {
        return index >= document.length();
    }
}
