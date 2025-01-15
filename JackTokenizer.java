import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JackTokenizer {
    private static BufferedReader reader;
    public static String currLine;
    private static String nextLine;
    public static String currToken;
    public static String nextToken;
    private static ArrayList<String> tokens ;
    private static int counter;
    private static HashMap<String,String> map ; 

    public JackTokenizer(File file) throws IOException {
        reader = new BufferedReader(new FileReader(file));
        currLine = null;
        nextLine = reader.readLine();
        tokens =  new ArrayList<>();
        map = new HashMap<>();
        counter = 0;
        buildMap();
        cleanFile();
    }

    public static void cleanFile() throws IOException {
        String[] dividers = {
            "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|",
            "<", ">", "=", "~", "class", "constructor", "function", "method", "field", "static", "var",
            "char", "boolean", "void", "true", "false", "null", "this", "let",
            "do", "if", "else", "while", "return"
        };
    
        HashSet<String> dividersSet = new HashSet<>(Arrays.asList(dividers));
    
        while (hasMorelines()) {
            if (currLine.trim().startsWith("//")) {
                advanceline(); // Skip comment-only lines
                continue;
            }
            int commentphrase = currLine.indexOf("/*");
            int endOfCommentPhrase = currLine.indexOf("*/");
            if (commentphrase != -1) {
                if(endOfCommentPhrase != -1){
                    advanceline();
                }
                else{
                    while(endOfCommentPhrase == -1 ){
                        advanceline(); // Skip comment-only lines
                        endOfCommentPhrase = currLine.indexOf("*/");
                    }
                    advanceline(); // Skip comment-only lines
                }
                continue;
            }
    
            int commentIndex = currLine.indexOf("//");
            if (commentIndex != -1) {
                currLine = currLine.substring(0, commentIndex).trim(); // Remove inline comments
            }
    
            if (currLine.isEmpty()) {
                advanceline();
                continue;
            }
    
            StringBuilder token = new StringBuilder();
            boolean insideString = false;
    
            for (int i = 0; i < currLine.length(); i++) {
                char c = currLine.charAt(i);
    
                if (c == '"') {
                    insideString = !insideString;
                    token.append(c);
                    if (!insideString) {
                        tokens.add(token.toString());
                        token.setLength(0);
                    }
                } else if (insideString) {
                    token.append(c);
                } else if (Character.isWhitespace(c)) {
                    if (token.length() > 0) {
                        tokens.add(token.toString());
                        token.setLength(0);
                    }
                } else if (dividersSet.contains(String.valueOf(c))) {
                    if (token.length() > 0) {
                        tokens.add(token.toString());
                        token.setLength(0);
                    }
                    tokens.add(String.valueOf(c));
                } else {
                    token.append(c);
                }
            }
    
            if (token.length() > 0) {
                tokens.add(token.toString());
            }
                advanceline(); // Move to the next line
           
        }
    }

    
    public static void buildMap() {
        // Keywords
        String[] keywords = {
            "class", "constructor", "function", "method", "field", "static", "var",
            "int", "char", "boolean", "void", "true", "false", "null", "this", "let",
            "do", "if", "else", "while", "return" 
        };

        // Symbols
        String[] symbols = {
            "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|",
            "<", ">", "=", "~"
        };

        // Add keywords to the map
        for (String keyword : keywords) {
            map.put(keyword, "KEYWORD");
        }

        // Add symbols to the map
        for (String symbol : symbols) {
            map.put(symbol, "SYMBOL");
        }
    }

    public static boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true; // Parsing was successful, so it's a valid integer
        } catch (NumberFormatException e) {
            return false; // Parsing failed, so it's not a valid integer
        }
    }

    public static boolean hasMorelines() throws IOException{
        while(nextLine != null && (nextLine.trim().isEmpty() || nextLine.startsWith("//"))){
            if (nextLine.trim().isEmpty() && currLine != null){
                break;
            }
            currLine =nextLine.trim();
            nextLine= reader.readLine();
        }    
        return (nextLine != null);
    }

    public static boolean hasMoreTokens() throws IOException{
        return counter != tokens.size();
    }

    public static void advanceline() throws IOException {
        currLine = nextLine;      
        nextLine= reader.readLine();
    }

    public static void advance() throws IOException {
        if (counter < tokens.size() - 1) { // Check if there are more tokens
            currToken = tokens.get(counter);
            counter++;
            nextToken = tokens.get(counter);
        } else if (counter < tokens.size()) { // Handle the last token
            currToken = tokens.get(counter);
            nextToken = null; // No next token available
            counter++;
        } else {
            throw new IndexOutOfBoundsException("No more tokens to advance.");
        }
    }


    public static String tokenType() throws IOException {
        // Check if the token is a keyword
        if (map.containsKey(currToken) && map.get(currToken).equals("KEYWORD")) {
            return "KEYWORD";
        }
    
        // Check if the token is a symbol
        if (currToken.length() == 1 && map.containsKey(currToken) && map.get(currToken).equals("SYMBOL")) {
            return "SYMBOL";
        }
    
        // Check if the token is an integer constant
        if (isValidInteger(currToken)) {
            return "INT_CONST";
        }
    
        // Check if the token is a string constant
        if (currToken.startsWith("\"") && currToken.endsWith("\"")) {
            return "STRING_CONST";
        }
    
        // Otherwise, it must be an identifier
        return "IDENTIFIER";
    }

    public static String keyWord() throws IOException {
        return  currToken.toUpperCase(); 
    }

    public static char symbol() throws IOException {
        return currToken.charAt(0);
    }

    public static String identifier() throws IOException {
        return currToken;
    }

    public static int intVal() throws IOException {
        return Integer.parseInt(currToken);
    }

    public static String stringVal() throws IOException {
        return currToken;
    }
    public void printAllTokens() {
        System.out.println("List of Tokens:");
        for (String token : tokens) {
            System.out.println(token);
        }
    }

    public void printAllSymbol() {
    System.out.println("List of symbols:");
    for (Map.Entry<String, String> entry : map.entrySet()) {
        System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
    }
}
}
