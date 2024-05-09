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

public class Main {

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

    private static String handleExpression(Snippet snippet, SnippetEvent event) {
        if (event.exception() != null) {
            return "| Exception: " + event.exception().getMessage() + " at " + snippet.source();
        } else if (event.value() != null) {
            return "| evaluated expression " + snippet.source() + " ==> " + event.value();
        } else {
            return "| no result for expression " + snippet.source();
        }
    }

    private static String handleTypeDeclaration(Snippet snippet) {
        String source = snippet.source().trim();
        if (snippet.subKind() == Snippet.SubKind.CLASS_SUBKIND) {
            return "| created class " + extractSimpleName(source);
        } else if (snippet.subKind() == Snippet.SubKind.INTERFACE_SUBKIND) {
            return "| created interface " + extractSimpleName(source);
        } else if (snippet.subKind() == Snippet.SubKind.ENUM_SUBKIND) {
            return "| created enum " + extractSimpleName(source);
        } else if (snippet.subKind() == Snippet.SubKind.ANNOTATION_TYPE_SUBKIND) {
            return "| created annotation " + extractSimpleName(source);
        } else {
            return "| created type " + extractSimpleName(source);
        }
    }

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

    private static String extractSimpleName(String source) {
        Pattern pattern = Pattern.compile("\\b(class|interface|enum|@interface|void|int|String|double|boolean|char)\\s+(\\w+)\\b");
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return "Unnamed";
    }

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



