import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;
import jdk.jshell.Snippet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class that reads Java snippets from a file and executes them using JShell.
 * It reads Java snippets from a specified file
 * and executes them using JShell. Snippets in the file are separated by two newlines.
 */

public class Main {

    /**
     * Main method that serves as the entry point of the program.
     * Reads Java snippets from a file and executes them.
     */
    public static void main(String[] args) {
        String filePath = "/Users/vladislav/Desktop/test.jsh.txt";
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new FileNotFoundException("Die Datei " + filePath + " wurde nicht gefunden.");
            }
            String content = new String(Files.readAllBytes(path));
            runCodeJshell(content.split("\n\n")); // Snippets sind durch zwei Newlines getrennt
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("SecurityException: Zugriff verweigert für " + filePath);
        } catch (IOException e) {
            System.out.println("IOException beim Lesen der Datei: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ein unbekannter Fehler ist aufgetreten: " + e.getMessage());
        }
    }


    /**
     * Executes an array of Java snippets using JShell. It processes each snippet and prints the result of each execution.
     * If a snippet is rejected, it prints the error message and any diagnostics.
     *
     * @param snippets Array of Java snippets to be executed.
     */
    public static void runCodeJshell(String[] snippets) {
        try (JShell shell = JShell.create()) {
            for (String snippet : snippets) {
                List<SnippetEvent> events = shell.eval(snippet);
                for (SnippetEvent event : events) {
                    if (event.status() == Snippet.Status.VALID) {
                        System.out.println(printResult(event));
                    } else if (event.status() == Snippet.Status.REJECTED) {
                        System.out.println("| Error in snippet: " + extractSimpleName(snippet) + " - " + (event.exception() != null ? event.exception().getMessage() : "Unknown error"));
                        shell.diagnostics(event.snippet()).forEach(diagnostic -> System.out.println("| Fehler: " + diagnostic.getMessage(Locale.ENGLISH)));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error during JShell execution: " + e.getMessage());
        }
    }

    /**
     * Generates a string representation of the result of a JShell snippet execution.
     * It handles different kinds of snippets such as expressions, type declarations, methods, variables, imports, and statements.
     *
     * @param event The snippet event containing the execution result.
     * @return A string representation of the execution result.
     */

    public static String printResult(SnippetEvent event) {
        Snippet snippet = event.snippet();
        String source = snippet.source().trim();
        switch (snippet.kind()) {
            case EXPRESSION:
                return handleExpression(snippet, event);
            case TYPE_DECL:
                return handleTypeDeclaration(snippet);
            case METHOD:
                return "| created method " + extractSimpleName(source);
            case VAR:
                return handleVariable(snippet, event);
            case IMPORT:
                return "| added import " + extractSimpleName(source);
            case STATEMENT:
                return "| executed statement " + snippet.source();
            default:
                return "| executed snippet " + snippet.source();
        }
    }

    /**
     * Handles the result of an expression snippet. It checks if the expression resulted in an exception or a value.
     * If there is an exception, it returns the exception message. If there is a value, it returns the value.
     *
     * @param snippet The snippet representing the expression.
     * @param event The snippet event containing the result of the expression execution.
     * @return A string representation of the expression result.
     */
    private static String handleExpression(Snippet snippet, SnippetEvent event) {
        if (event.exception() != null) {
            return "| Exception: " + event.exception().getMessage() + " at " + snippet.source();
        } else if (event.value() != null) {
            return "| evaluated expression " + snippet.source() + " ==> " + event.value();
        } else {
            return "| no result for expression " + snippet.source();
        }
    }

    /**
     * Handles the result of a type declaration snippet. It determines the type of declaration
     * (class, interface, enum, annotation) and returns a string indicating the type and its name.
     *
     * @param snippet The snippet representing the type declaration.
     * @return A string representation of the type declaration result.
     */
    private static String handleTypeDeclaration(Snippet snippet) {
        String source = snippet.source().trim();
        if (snippet.subKind() == Snippet.SubKind.CLASS_SUBKIND) {
            return "| created class " + extractSimpleName(source);
        } else if (snippet.subKind() == Snippet.SubKind.INTERFACE_SUBKIND) {
            return "| created interface " + extractSimpleName(source);
        } else if (snippet.subKind() == Snippet.SubKind.ENUM_SUBKIND) {
            return "| created enum " + extractSimpleName(source);
        } else if (snippet.subKind() == Snippet.SubKind.ANNOTATION_TYPE_SUBKIND) {
            return "| created annotation interface " + extractSimpleName(source);
        } else {
            return "| created type " + extractSimpleName(source);
        }
    }

    /**
     * Handles the result of a variable snippet. It checks if the variable initialization resulted in an exception
     * or a value. If there is an exception, it returns the exception message. If there is a value, it returns the value.
     * If there is no value, it returns a default value based on the variable type.
     *
     * @param snippet The snippet representing the variable.
     * @param event The snippet event containing the result of the variable initialization.
     * @return A string representation of the variable initialization result.
     */
    private static String handleVariable(Snippet snippet, SnippetEvent event) {
        String source = snippet.source().trim();
        String varName = source.split("=")[0].trim();
        if (event.exception() != null) {
            return "| Exception: " + event.exception().getMessage() + " at " + snippet.source();
        }
        if (event.value() != null) {
            return varName + " ==> " + event.value();
        } else {
            String defaultValue = getDefaultValue(varName, source);
            return varName + " ==> " + defaultValue;
        }
    }

    /**
     * Extracts the simple name from a Java source code snippet. It uses a regular expression
     * to find the name of the class, interface, enum, annotation, method, or variable.
     *
     * @param source The source code of the snippet.
     * @return The simple name extracted from the source code.
     */
    private static String extractSimpleName(String source) {
        Pattern pattern = Pattern.compile("\\b(class|interface|enum|@interface|void|int|String|double|boolean|char)\\s+(\\w+)\\b");
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return "Unnamed";
    }

    /**
     * Gets the default value for a variable based on its type. It checks the type of the variable
     * in the source code and returns the corresponding default value.
     *
     * @param varName The variable name.
     * @param sourceCode The source code containing the variable declaration.
     * @return The default value for the variable.
     */
    private static String getDefaultValue(String varName, String sourceCode) {
        if (sourceCode.contains("int")) {
            return "0";
        } else if (sourceCode.contains("String")) {
            return "\"\"";
        } else if (sourceCode.contains("boolean")) {
            return "false";
        } else if (sourceCode.contains("float") || sourceCode.contains("double")) {
            return "0.0";
        } else if (sourceCode.contains("char")) {
            return "'\\u0000'";
        } else {
            return "null";
        }
    }
}



