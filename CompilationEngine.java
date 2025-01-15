import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class CompilationEngine {

    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static String currentToken;
    private static JackTokenizer tokenizer;
    private static String tempString;
    private static int indentLevel ;
    private static String tempType;
    private static boolean type;
    private static SymbolTable symbolTable;
    private static String className;


    public CompilationEngine (File inputFile, File outputFile) throws IOException{
        reader = new BufferedReader(new FileReader(inputFile));
        writer = new BufferedWriter(new FileWriter(outputFile));
        tokenizer = new JackTokenizer(inputFile);
        symbolTable = new SymbolTable();
        indentLevel = 0;
        tempType = "";
        tempString = "";
        className = "";
        type = false;
        tokenizer.advance(); // Advance to the first token
        currentToken = tokenizer.currToken; // Initialize currentToke
        compileClass(); // Start compilation       
        writer.flush();
        writer.close();
    }
    
    public static void compileClass() throws IOException {
        writer.write("<class>\n");
        indentLevel++; // Increase indentation for the block
        process("class");
        className = currentToken;
        process(currentToken);
        process("{");
       
        while( currentToken.equals("field") || currentToken.equals("static") ){
            compileClassVarDec();
        }
         
        while (currentToken.equals("constructor") || currentToken.equals("method") || currentToken.equals("function")) { 
            compileSubroutine();
        }
        process("}");
        indentLevel--; // Decrease indentation after closing block
        writer.write("</class>\n");
    }

    public static void compileClassVarDec() throws IOException {

        writer.write(getIndentation() + "<classVarDec>\n");
        indentLevel++; // Increase indentation for the block
        String currtype;
        if (currentToken.equals("static") ){
            process("static");
            currtype = currentToken;
            process(currentToken);
            symbolTable.define(currentToken, currtype , SymbolTable.Kind.STATIC);
            process(currentToken);
            while (!currentToken.equals(";")){
                process(",");
                process(currentToken);
            }
            process(";");
        }

        else if (currentToken.equals("field")){
            process("field");
            currtype = currentToken;
            process(currentToken);
            symbolTable.define(currentToken, currtype , SymbolTable.Kind.THIS);            
            process(currentToken);
            while (!currentToken.equals(";")){
                process(",");
                process(currentToken);
            }
            process(";");
        }
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</classVarDec>\n");
    }


    public static void compileSubroutine() throws IOException {
        symbolTable.reset();
        writer.write(getIndentation() + "<subroutineDec>\n");
        indentLevel++; // Increase indentation for the block

        if (currentToken.equals("constructor")){
            process("constructor");
            if(currentToken.equals("void")){
                process("void");
            }
            else{
                process(currentToken);
            }
        }
        else if (currentToken.equals("method")){
            symbolTable.define("this",className , SymbolTable.Kind.ARG);
            process("method");
            if(currentToken.equals("void")){
                process("void");
            }
            else{
                process(currentToken);
            }

        }
        else if (currentToken.equals("function")){
            process("function");
            if(currentToken.equals("void")){
                process("void");
            }
            else{
                process(currentToken);
            }
        }
        
        process(currentToken);
        process("(");
        compileParamaterList();
        process(")");
        compileSubroutineBody();
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</subroutineDec>\n");
    }


    public static void compileParamaterList() throws IOException {
        writer.write(getIndentation() + "<parameterList>\n");
        indentLevel++; // Increase indentation for the block
        String currtype;
        if (!currentToken.equals(")")){      
            currtype = currentToken;
            process(currentToken);
            symbolTable.define(currentToken, currtype, SymbolTable.Kind.ARG);
            process(currentToken);
            while (!currentToken.equals(")")){
                process(",");
                currtype = currentToken;
                process(currentToken);
                symbolTable.define(currentToken, currtype, SymbolTable.Kind.ARG);
                process(currentToken);
            }
        }
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</parameterList>\n");
    }


    public static void compileSubroutineBody() throws IOException {
        writer.write(getIndentation() + "<subroutineBody>\n");
        indentLevel++; // Increase indentation for the block
        process("{");
        while (currentToken.equals("var")){
           complieVarDec();
        }
        compileStatements();
        process("}");
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</subroutineBody>\n");
    }


    public static void complieVarDec() throws IOException {
        writer.write(getIndentation() + "<varDec>\n");
        indentLevel++; // Increase indentation for the block
        String currtype;

        process("var");
        currtype = currentToken;
        process(currentToken);
        symbolTable.define(currentToken, currtype, SymbolTable.Kind.LOCAL);
        process(currentToken);
        while (!currentToken.equals(";")){
            process(",");
            symbolTable.define(currentToken, currtype, SymbolTable.Kind.LOCAL); 
            process(currentToken);
        }   
        process(";");
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</varDec>\n");
     }


    public static void compileStatements() throws IOException {
        writer.write(getIndentation() + "<statements>\n");
        indentLevel++; // Increase indentation for the block
        while(currentToken.equals("let") || currentToken.equals("do") || currentToken.equals("if") || currentToken.equals("while") || currentToken.equals("return")){
            if (currentToken.equals("let")){
                compileLet();
            }
            else if (currentToken.equals("if")){
                compileIf();
            }
            else if (currentToken.equals("do")){
                compileDo();
            }
            else if (currentToken.equals("while")){
                compileWhile();
            }
            else if (currentToken.equals("return")){
                compileReturn();
            }
        }
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</statements>\n");
    }

    public static void compileLet() throws IOException {
        writer.write(getIndentation() + "<letStatement>\n");
        indentLevel++; // Increase indentation for the block

        process("let");
        process(currentToken);
        if ( currentToken.equals("[") ) {
            process("[");
            compileExpression();
            process("]");
        }
        process("=");
        compileExpression();
        process(";");
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</letStatement>\n");
    }

    public static void compileIf () throws IOException {
        writer.write(getIndentation() + "<ifStatement>\n");
        indentLevel++; // Increase indentation for the block

        process("if");
        process("(");
        compileExpression();
        process(")");
        process("{");
        compileStatements();
        process("}");
        if (currentToken.equals("else")){
            process("else");
            process("{");
            compileStatements();
            process("}");
        }
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</ifStatement>\n"); 
    }

    public static void compileWhile() throws IOException {
        writer.write(getIndentation() + "<whileStatement>\n");
        indentLevel++; // Increase indentation for the block

        process("while");
        process("(");
        compileExpression();
        process(")");
        process("{");
        compileStatements();
        process("}");
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</whileStatement>\n");
    }

    public static void compileDo() throws IOException {
        writer.write(getIndentation() + "<doStatement>\n");
        indentLevel++; // Increase indentation for the block
        process("do");

        if ( tokenizer.tokenType().equals("IDENTIFIER") ) { 

            tempString = currentToken;
            tokenizer.advance();
            currentToken = tokenizer.currToken;

            if (currentToken.equals("(")) {
                type = true;
                process(tempString);
                type = false;
                process("(");
                compileExpressionList();
                process(")");
            }   
            else if (currentToken.equals(".")) {
                type = true;
                process(tempString);
                type = false;
                process(".");
                compileTerm();
               
            }
            else{
                type = true;
                process(tempString);
                type = false;
            }
        }

        process(";");
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</doStatement>\n");
    }

    public static void compileReturn() throws IOException {
        writer.write(getIndentation() + "<returnStatement>\n");
        indentLevel++; // Increase indentation for the block
        process("return");
        if (!currentToken.equals(";")){
            compileExpression();
        }
        process(";"); 
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</returnStatement>\n");   
    }

    public static void compileExpression() throws IOException {
        writer.write(getIndentation() + "<expression>\n");
        indentLevel++; // Increase indentation for the block
        writer.write(getIndentation() + "<term>\n");
        indentLevel++; // Increase indentation for the bloc
        compileTerm();
        indentLevel--; 
        writer.write(getIndentation() + "</term>\n");
        
        while(currentToken.equals("+") || currentToken.equals("-") || currentToken.equals("*") || currentToken.equals("/") || currentToken.equals("&") || currentToken.equals("|") || currentToken.equals("<") ||currentToken.equals(">") || currentToken.equals("=")){
            process(currentToken);
            writer.write(getIndentation() + "<term>\n");
            indentLevel++; // Increase indentation for the bloc
            compileTerm();
            indentLevel--; 
            writer.write(getIndentation() + "</term>\n");
           
        }
        indentLevel--; // Decrease indentation after closing block
        writer.write(getIndentation() + "</expression>\n");
    }
    
    public static void compileTerm() throws IOException {
       
        if ( tokenizer.tokenType().equals("INT_CONST") ) {
            process(currentToken);
        }

        if ( tokenizer.tokenType().equals("STRING_CONST") ) {
            process(currentToken);
        }

        if ( tokenizer.tokenType().equals("KEYWORD") ) {
            process(currentToken);
        }
        if ( tokenizer.tokenType().equals("SYMBOL") ){

            if( currentToken.equals("(")){
                process("(");
                compileExpression();
                process(")");
            }
            else if(currentToken.equals("~") || currentToken.equals("-")) {
                process(currentToken);
                writer.write(getIndentation() + "<term>\n");
                indentLevel++; // Increase indentation for the bloc
                compileTerm();
                indentLevel--; 
                writer.write(getIndentation() + "</term>\n");
            }
        }

        if ( tokenizer.tokenType().equals("IDENTIFIER") ) { 
            tempString = currentToken;
            tempType = tokenizer.tokenType();
            tokenizer.advance();
            currentToken = tokenizer.currToken;

            if (currentToken.equals("(")) {
                type = true;
                process(tempString);
                type = false;
                process("(");
                compileExpressionList();
                process(")");
            }
            else if (currentToken.equals("[")) {
                type = true;
                process(tempString);
                type = false;
                process("[");
                compileExpression(); 
                process("]");
            }
            else if (currentToken.equals(".")) {
                type = true;
                process(tempString);
                type = false;
                process(".");
                compileTerm();
            }
            else{
                type = true;
                process(tempString);
                type = false;
            }
        }
        
    }

    public static void compileExpressionList() throws IOException {
        writer.write(getIndentation() + "<expressionList>\n");
        indentLevel ++ ;
        if (!currentToken.equals(")")) {
            compileExpression();
        }
       
        while (!currentToken.equals(";")) {
            // Break the loop if the current token is ")" and the next token is ";"
            if (currentToken.equals(")") && tokenizer.nextToken.equals(";")) {
                break;
            }
    
            if (currentToken.equals(",")) {
                process(",");
                compileExpression();
            }
        }
        indentLevel --;
        writer.write(getIndentation() + "</expressionList>\n");
    }

    public static void process(String str) throws IOException {
        if (currentToken.equals(str)) {
            printXMLToken(currentToken);
            if (tokenizer.hasMoreTokens()) {
                tokenizer.advance(); 
                currentToken = tokenizer.currToken;
            }
        } 
        else if (tempString.equals(str)){
            printXMLToken(tempString);
        }
        else {
            writer.write("syntax error: expected " + str + " but found " + currentToken + "\n");
        }
    }

    public static void printXMLToken(String str) throws IOException {
        String condition = tokenizer.tokenType();
        if (type){
            condition = tempType;
        }
        switch (condition) {

            case "KEYWORD":
                writer.write( getIndentation() + "<keyword> " + str + " </keyword>\n");
                break;
    
            case "SYMBOL":
                str = switch (str) {
                    case "<" -> "&lt;";
                    case ">" -> "&gt;";
                    case "&" -> "&amp;";
                    case "\"" -> "&quot;";
                    default -> str;
                };
                writer.write(getIndentation() +  "<symbol> " + str + " </symbol>\n");
                break;
    
            case "INT_CONST":
                writer.write( getIndentation() + "<integerConstant> " + str + " </integerConstant>\n");
                break;
    
            case "STRING_CONST":
                writer.write( getIndentation() + "<stringConstant> " + str.substring(1 , str.length() -1 ) + " </stringConstant>\n");
                break;
    
            case "IDENTIFIER":
                writer.write( getIndentation() + "<identifier> " + str +  " </identifier>\n");
                break;
    
            default:
                writer.write("Unknown token type: " + str + "\n");
        }
    }

    private static String getIndentation() {
        return "  ".repeat(indentLevel); 
    }

    
}
