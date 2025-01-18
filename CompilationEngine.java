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
    private static String tempKind;
    private static int tempIndex;
    private static SymbolTable symbolTable;
    private static String className;
    private static String currOp;
    private static String negORNot;
    private static VMWriter vmWriter;
    private static int labelIndex; 
    private static int numOfParm; 
    private static int numOfexp;



    public CompilationEngine (File inputFile, File outputFile) throws IOException{
        reader = new BufferedReader(new FileReader(inputFile));
        writer = new BufferedWriter(new FileWriter(outputFile));
        tokenizer = new JackTokenizer(inputFile);
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter(outputFile);
        tempKind = "";
        tempIndex = 0 ;
        numOfParm = 0 ; 
        labelIndex = 1 ;
        numOfexp = 0 ; 
        tempString = "";
        className = "";
        negORNot = "";
        currOp = "";
        tokenizer.advance(); // Advance to the first token
        currentToken = tokenizer.currToken; // Initialize currentToke
        compileClass(); // Start compilation       
        writer.flush();
        writer.close();
    }
   
    public static void compileClass()  throws IOException{
        tokenizer.advance();
        className = currentToken;
        tokenizer.advance();
        tokenizer.advance();

        while( currentToken.equals("field") || currentToken.equals("static") ){
            compileClassVarDec();
        }

        while (currentToken.equals("constructor") || currentToken.equals("method") || currentToken.equals("function")) { 
            compileSubroutine();
        }
        tokenizer.advance();

    }
   
    public static void compileClassVarDec() throws IOException{

        String currtype;
        if (currentToken.equals("static") ){
            tokenizer.advance();
            currtype = currentToken;
            tokenizer.advance();
            symbolTable.define(currentToken, currtype , SymbolTable.Kind.STATIC);
            tokenizer.advance();
            while (!currentToken.equals(";")){
                tokenizer.advance();
                tokenizer.advance();
            }
            tokenizer.advance();
        }   
        else if (currentToken.equals("field")){
            tokenizer.advance();
            currtype = currentToken;
            tokenizer.advance();
            symbolTable.define(currentToken, currtype , SymbolTable.Kind.THIS);            
            tokenizer.advance();
            while (!currentToken.equals(";")){
                tokenizer.advance();
                tokenizer.advance();
            }
            tokenizer.advance();
        }
     }
   
    public static void compileSubroutine() throws IOException{
        symbolTable.reset(); // Reset subroutine-level symbol table

        String functionType = currentToken; // "constructor", "function", or "method"
        tokenizer.advance();
        String returnType = currentToken;     // "void", "int", etc.
        tokenizer.advance();
        String functionName = currentToken; // functionName name
        tokenizer.advance();

        if (functionType.equals("method")) {
            symbolTable.define("this", className, SymbolTable.Kind.ARG);
        }

        tokenizer.advance();
        compileParameterList();
        tokenizer.advance();

        tokenizer.advance();
        compileVarDec(); // Parse and define local variables

        // Write VM function declaration
        int nVars = symbolTable.varCount(SymbolTable.Kind.LOCAL);
        vmWriter.writeFunction(className + "." + functionName, nVars);

        if (functionType.equals("constructor")) {
            vmWriter.writePush(VMWriter.Segment.CONSTANT, symbolTable.varCount(SymbolTable.Kind.THIS));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(VMWriter.Segment.POINTER, 0);
        } else if (functionType.equals("method")) {
            vmWriter.writePush(VMWriter.Segment.ARGUMENT, 0);
            vmWriter.writePop(VMWriter.Segment.POINTER, 0);
        }
        compileStatements(); // Parse and compile statements
        tokenizer.advance();
    }
    
    public static void compileParameterList() throws IOException {
        String currtype;

        if (!currentToken.equals(")")){      
            currtype = currentToken;
            tokenizer.advance();
            symbolTable.define(currentToken, currtype, SymbolTable.Kind.ARG);
            tokenizer.advance();
            while (!currentToken.equals(")")){
                tokenizer.advance();
                currtype = currentToken;
                tokenizer.advance();
                symbolTable.define(currentToken, currtype, SymbolTable.Kind.ARG);
                tokenizer.advance();
            }
        }
    }
   
    public static void compileSubroutineBody() throws IOException {

        tokenizer.advance();
        while (currentToken.equals("var")){
           compileVarDec();
        }
        compileStatements();
        tokenizer.advance();

    }

   public static void compileVarDec() throws IOException {
       
        String currtype;

        tokenizer.advance();
        currtype = currentToken;
        tokenizer.advance();
        symbolTable.define(currentToken, currtype, SymbolTable.Kind.LOCAL);
        tokenizer.advance();

        while (!currentToken.equals(";")){
            tokenizer.advance();
            symbolTable.define(currentToken, currtype, SymbolTable.Kind.LOCAL); 
            tokenizer.advance();
        }   
        
        tokenizer.advance();
     }
   
     public static void compileStatements() throws IOException {
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
    }
   
    public static void compileDo() throws IOException {
            tokenizer.advance();

        if ( tokenizer.tokenType().equals("IDENTIFIER") ) { 

            tempString = currentToken;
            tokenizer.advance();
            currentToken = tokenizer.currToken;

            if (currentToken.equals("(")) {
                tokenizer.advance();
                compileExpressionList();
                tokenizer.advance();
               
            }   
            else if (currentToken.equals(".")) {
                tempString += currentToken;
                tokenizer.advance();
                currentToken = tokenizer.currToken;
                tempString += currentToken;
                compileTerm();
            }
            
        }
        vmWriter.writeCall(tempString, numOfParm);
        tempString = "";

        vmWriter.writePop(VMWriter.Segment.TEMP, 0);
        tokenizer.advance();
    }

    public static void compileReturn() throws IOException {    
        tokenizer.advance();
        if (currentToken.equals(";")){
            vmWriter.writePush(VMWriter.Segment.CONSTANT, 0);
            vmWriter.writeReturn();
        }
        if (!currentToken.equals(";")){
            compileExpression();
            vmWriter.writeReturn();
        }
        tokenizer.advance();
    }
    
    public static void compileLet() throws IOException{
     
        tokenizer.advance();

        currentToken = tokenizer.currToken;
        tempString = currentToken;
        tempKind = symbolTable.kindOf(tempString);
        tempIndex = symbolTable.indexOf(tempString);
        tokenizer.advance();

        if (currentToken.equals("[") ) {

            vmWriter.writePush(VMWriter.Segment.valueOf(tempKind.toUpperCase()),tempIndex);
            tokenizer.advance();

            compileExpression();

            tokenizer.advance();
            vmWriter.writeArithmetic(VMWriter.Command.ADD);
        
            tokenizer.advance();
            compileExpression();
            tokenizer.advance();        

            vmWriter.writePop(VMWriter.Segment.POINTER, 1);
            vmWriter.writePush(VMWriter.Segment.THAT, 0);
            vmWriter.writePop(VMWriter.Segment.TEMP, 0);
            vmWriter.writePop(VMWriter.Segment.POINTER, 1);
            vmWriter.writePush(VMWriter.Segment.TEMP, 0);
            vmWriter.writePop(VMWriter.Segment.THAT, 0);
        }
        else{

            tokenizer.advance();
            compileExpression();
            tokenizer.advance();

            vmWriter.writePop(VMWriter.Segment.valueOf(tempKind.toUpperCase()),tempIndex);

        }

    }

    public static void compileWhile() throws IOException {

        vmWriter.writeLabel("Label L" + labelIndex);
        labelIndex ++ ; 
        tokenizer.advance();
        tokenizer.advance();

        compileExpression();

        tokenizer.advance();
        tokenizer.advance();

        vmWriter.writeArithmetic(VMWriter.Command.NOT);
        vmWriter.writeIf("if-goto L" + labelIndex );

        compileStatements();

        vmWriter.writeGoto("goto L" +  (labelIndex - 1));
        vmWriter.writeLabel("Label L" + labelIndex);
        labelIndex ++ ; 

        tokenizer.advance();

    }
  
   
    public static void compileIf () throws IOException {
        tokenizer.advance();
        tokenizer.advance();

        compileExpression();

        vmWriter.writeArithmetic(VMWriter.Command.NOT);
        vmWriter.writeIf("if-goto L" + labelIndex );
        labelIndex ++ ; 

        tokenizer.advance();
        tokenizer.advance();

        compileStatements();

        vmWriter.writeGoto("goto L" +  (labelIndex));
        vmWriter.writeLabel("Label L" + (labelIndex -1));
       

        tokenizer.advance();
        currentToken = tokenizer.currToken;
        
        if (currentToken.equals("else")){

            tokenizer.advance();
            tokenizer.advance();

            compileStatements();
            vmWriter.writeLabel("Label L" +labelIndex );

            tokenizer.advance();
        }
        labelIndex ++ ; 

    }
    
    public static void compileExpression() throws IOException {
       
        compileTerm();
        while(currentToken.equals("+") || currentToken.equals("-") || currentToken.equals("*") || currentToken.equals("/") || currentToken.equals("&") || currentToken.equals("|") || currentToken.equals("<") ||currentToken.equals(">") || currentToken.equals("=")){
            currOp = tokenizer.currToken;
            tokenizer.advance();
            compileTerm(); 
            switch (currOp) {
                case "+" :
                    vmWriter.writeArithmetic(VMWriter.Command.ADD);
                    break;
                case "*":
                    vmWriter.writeCall("Math.multiply" , 2);
                    break;
                case "/":
                vmWriter.writeCall("Math.divide" , 2);
                    break;
                case "|":
                    vmWriter.writeArithmetic(VMWriter.Command.OR);
                    break;
                case ">":
                    vmWriter.writeArithmetic(VMWriter.Command.GT);
                    break;
                case "<":
                    vmWriter.writeArithmetic(VMWriter.Command.LT);
                    break;
                case "&":
                    vmWriter.writeArithmetic(VMWriter.Command.AND);
                    break;
                case "=":
                    vmWriter.writeArithmetic(VMWriter.Command.EQ);
                    break;
            }

        }
       
    }
  
    public static void compileTerm() throws IOException {
       
        if ( tokenizer.tokenType().equals("INT_CONST") ) {
            vmWriter.writePush(VMWriter.Segment.CONSTANT, Integer.parseInt(tokenizer.currToken));
            tokenizer.advance();
        }

        else if ( tokenizer.tokenType().equals("STRING_CONST") ) {
            // Handle string constant
            String stringVal = tokenizer.currToken.substring(1, tokenizer.currToken.length() - 1 ); // Get the string constant value
           
            vmWriter.writePush(VMWriter.Segment.CONSTANT, stringVal.length());
            vmWriter.writeCall("String.new", 1);

            for (int i = 0; i <  stringVal.length(); i++) {

                char c = stringVal.charAt(i);
                vmWriter.writePush(VMWriter.Segment.CONSTANT, (int) c); // Push ASCII value of character
                vmWriter.writeCall("String.appendChar", 2); // Call String.appendChar
            }
            tokenizer.advance();
        }

        else if ( tokenizer.tokenType().equals("KEYWORD") ) {

            if ( tokenizer.currToken.equals("true") ) {
                vmWriter.writePush(VMWriter.Segment.CONSTANT, 1);
                vmWriter.writeArithmetic(VMWriter.Command.NEG);
            } 
            else if ( tokenizer.currToken.equals("false") || tokenizer.currToken.equals("null") ) {
                vmWriter.writePush(VMWriter.Segment.CONSTANT, 0);
            }
            else if ( tokenizer.currToken.equals("this") ) {
                vmWriter.writePush(VMWriter.Segment.POINTER, 0);
            }

            tokenizer.advance();
        }

        else if ( tokenizer.tokenType().equals("SYMBOL") ){

            if( currentToken.equals("(")){
                tokenizer.advance();
                compileExpression();
                tokenizer.advance();
            }

            else if(currentToken.equals("~") || currentToken.equals("-")) {
                negORNot = tokenizer.currToken;
                tokenizer.advance();
                compileTerm();
                switch (negORNot) {
                    case "~" :
                        vmWriter.writeArithmetic(VMWriter.Command.NOT);
                        break;
                    case "-":
                        vmWriter.writeArithmetic(VMWriter.Command.NEG);
                        break;
                }   
            }
        }

        if ( tokenizer.tokenType().equals("IDENTIFIER") ) { 
            tempString = tokenizer.currToken;
            tokenizer.advance();
            currentToken = tokenizer.currToken;

            if (currentToken.equals("(")) {
                tokenizer.advance();
                compileExpressionList();
                vmWriter.writeCall(tempString, numOfexp);
                tokenizer.advance();
            }
            else if (currentToken.equals("[")) {
                tokenizer.advance();
                compileExpression(); 
                tokenizer.advance();
            }
            else if (currentToken.equals(".")) {
                tokenizer.advance();
                compileTerm();
            }
        }
    }
   
    public static int compileExpressionList() throws IOException{
        if (!currentToken.equals(")")) {
            compileExpression();
            numOfexp ++ ;
        }
        while (!currentToken.equals(";")) {
            // Break the loop if the current token is ")" and the next token is ";"
            if (currentToken.equals(")") && tokenizer.nextToken.equals(";")) {
                break;
            }
    
            if (currentToken.equals(",")) {
                tokenizer.advance();
                compileExpression();
                numOfexp ++ ;
            }
        }
        return numOfexp;
    }
}