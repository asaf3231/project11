import java.io.File;
import java.io.IOException;
public class CompilationEngine {

   
    private static JackTokenizer tokenizer;
    private static String tempString;
    private static String tempKind;
    private static int tempIndex;
    private static SymbolTable symbolTable;
    private static String className;
    private static String negORNot;
    private static VMWriter vmWriter;
    private static int labelIndex; 
    private static int numOfParm; 
    private static int numOfexp;
    private static String functionType;
    private static String returnType;
    private static String functionName;
    private static Identifier identifier;
    private static int ifIndex;  
    private static int whileIndex; 
    private static String doTempStr;
    private static int parenthesisCounter;
    private static boolean isObj;
    private static boolean isObjLet;
    private static int indexOfObject;

    public CompilationEngine (File inputFile, File outputFile) throws IOException{
        tokenizer = new JackTokenizer(inputFile);
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter(outputFile);
        tempKind = "";
        tempIndex = 0 ;
        numOfParm = 0 ; 
        ifIndex = 0;
        whileIndex = 0;
        labelIndex = 0 ;
        isObj = false;
        isObjLet = false;
        indexOfObject = 0;
        parenthesisCounter = 0 ; 
        numOfexp = 0 ; 
        tempString = "";
        className = "";
        negORNot = "";
        tokenizer.advance(); // Advance to the first token
        compileClass(); // Start compilation 
        vmWriter.writer.flush();
        vmWriter.writer.close();
    }
   
    public static void compileClass()  throws IOException{
        tokenizer.advance();
        className = tokenizer.currToken;
        tokenizer.advance();
        tokenizer.advance();

        while( tokenizer.currToken.equals("field") || tokenizer.currToken.equals("static") ){
            compileClassVarDec();
        }

        while (tokenizer.currToken.equals("constructor") || tokenizer.currToken.equals("method") || tokenizer.currToken.equals("function")) { 
            compileSubroutine();
        }
    }
   
    public static void compileClassVarDec() throws IOException{

        String currtype;

        if (tokenizer.currToken.equals("static") ){
            tokenizer.advance();
            currtype = tokenizer.currToken;
            tokenizer.advance();
            symbolTable.define(tokenizer.currToken, currtype , SymbolTable.Kind.STATIC);
            tokenizer.advance();
            while (!tokenizer.currToken.equals(";")){
                tokenizer.advance();
                symbolTable.define(tokenizer.currToken, currtype , SymbolTable.Kind.STATIC);
                tokenizer.advance();
            }
            tokenizer.advance();
        }   

        else if (tokenizer.currToken.equals("field")){
            tokenizer.advance();
            currtype = tokenizer.currToken;
            tokenizer.advance();
            symbolTable.define(tokenizer.currToken, currtype , SymbolTable.Kind.THIS);       
            tokenizer.advance();
            while (!tokenizer.currToken.equals(";")){
                tokenizer.advance();
                symbolTable.define(tokenizer.currToken, currtype , SymbolTable.Kind.THIS);
                tokenizer.advance();
            }
            tokenizer.advance();
        }
     }
   
    public static void compileSubroutine() throws IOException{
        symbolTable.reset(); // Reset subroutine-level symbol table

        functionType = tokenizer.currToken; // "constructor", "function", or "method"
        tokenizer.advance();
        returnType = tokenizer.currToken;     // "void", "int", etc.
        tokenizer.advance();
         functionName = tokenizer.currToken; // functionName name
        tokenizer.advance();

        if (functionType.equals("method")) {
            symbolTable.define("this", className, SymbolTable.Kind.ARGUMENT);
        }

        tokenizer.advance();
        compileParameterList();
        tokenizer.advance();
        compileSubroutineBody(); // Parse and define local variables

    }
    
    public static void compileParameterList() throws IOException {
        String currtype;
        if (!tokenizer.currToken.equals(")")){      
            currtype = tokenizer.currToken;
            tokenizer.advance();
            symbolTable.define(tokenizer.currToken, currtype, SymbolTable.Kind.ARGUMENT);
            tokenizer.advance();
            while (!tokenizer.currToken.equals(")")){
                tokenizer.advance();
                currtype = tokenizer.currToken;
                tokenizer.advance();
                symbolTable.define(tokenizer.currToken, currtype, SymbolTable.Kind.ARGUMENT);
                tokenizer.advance();
            }
        }
    }
   
    public static void compileSubroutineBody() throws IOException {

        tokenizer.advance();

        while (tokenizer.currToken.equals("var")){
           compileVarDec();
        }

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

       
        compileStatements();
        tokenizer.advance();
    }

   public static void compileVarDec() throws IOException {
       
        String currtype;

        tokenizer.advance();
        currtype = tokenizer.currToken;
        tokenizer.advance();
        symbolTable.define(tokenizer.currToken, currtype, SymbolTable.Kind.LOCAL);

        tokenizer.advance();
        while (!tokenizer.currToken.equals(";")){
            tokenizer.advance();
            symbolTable.define(tokenizer.currToken, currtype, SymbolTable.Kind.LOCAL); 
            tokenizer.advance();
        }   
        tokenizer.advance();
     }
   
     public static void compileStatements() throws IOException {
        
        while(tokenizer.currToken.equals("let") || tokenizer.currToken.equals("do") || tokenizer.currToken.equals("if") || tokenizer.currToken.equals("while") || tokenizer.currToken.equals("return")){
            if (tokenizer.currToken.equals("let")){
                compileLet();
            }
            else if (tokenizer.currToken.equals("if")){
                compileIf();
            }
            else if (tokenizer.currToken.equals("do")){
                compileDo();
            }
            else if (tokenizer.currToken.equals("while")){
                compileWhile();
            }
            else if (tokenizer.currToken.equals("return")){
                compileReturn();
            }
        }
    }
   
    public static void compileDo() throws IOException {
            tokenizer.advance();
            Boolean methodInClass = true;

        if ( tokenizer.tokenType().equals("IDENTIFIER") ) { 
            String temp = tokenizer.currToken; 

            if (symbolTable.methodLevelMap.containsKey(temp) || symbolTable.classLevelMap.containsKey(temp) ){
                isObj =true;
                vmWriter.writePush(VMWriter.Segment.valueOf(symbolTable.kindOf(temp)), symbolTable.indexOf(temp));
                temp = symbolTable.typeOf(temp);
            }

            tokenizer.advance();

            if (tokenizer.currToken.equals("(")) {
                tokenizer.advance();
                if (methodInClass){
                    vmWriter.writePush(VMWriter.Segment.POINTER, 0);
                }
                compileExpressionList();
                if (methodInClass){
                    vmWriter.writeCall(className + "." + temp, numOfexp + 1 );
                }
                tokenizer.advance();
            }   
            else if (tokenizer.currToken.equals(".")) {
                methodInClass =false;
                tempString += temp;
                tempString += tokenizer.currToken;
                tokenizer.advance();
                compileTerm();
            }
        }
        vmWriter.writePop(VMWriter.Segment.TEMP, 0);
        tokenizer.advance();
    }

    public static void compileReturn() throws IOException {    

        tokenizer.advance();
    

        if (tokenizer.currToken.equals(";")){
            vmWriter.writePush(VMWriter.Segment.CONSTANT, 0);
            vmWriter.writeReturn();
        }

        else if (!tokenizer.currToken.equals(";")){
            compileExpression();
            vmWriter.writeReturn();
        }

        tokenizer.advance();      

    }
    
    public static void compileLet() throws IOException{

        tokenizer.advance();
        tempKind = symbolTable.kindOf(tokenizer.currToken);
        tempIndex = symbolTable.indexOf(tokenizer.currToken);
        tokenizer.advance();

        if (tokenizer.currToken.equals("[") ) {

            tokenizer.advance();

            compileExpression();
            vmWriter.writePush(VMWriter.Segment.valueOf(tempKind.toUpperCase()),tempIndex);
            tokenizer.advance();
            vmWriter.writeArithmetic(VMWriter.Command.ADD);
        
            tokenizer.advance();
            compileExpression();
            tokenizer.advance();        



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

        int rememberwhile = labelIndex;
        vmWriter.writeLabel("label "+ className+ "_" + rememberwhile);
        labelIndex ++ ; 
        labelIndex ++ ; 
        tokenizer.advance();
        tokenizer.advance();

        compileExpression();
        tokenizer.advance();
        tokenizer.advance();

        vmWriter.writeArithmetic(VMWriter.Command.NOT);
        vmWriter.writeIf("if-goto "  + className + "_" + (rememberwhile + 1));
        
        compileStatements();

        vmWriter.writeGoto("goto " + className + "_" +  (rememberwhile));
        vmWriter.writeLabel("label "+ className+ "_" + (rememberwhile + 1));
         

        tokenizer.advance();

    }
  
   
    public static void compileIf () throws IOException {
        tokenizer.advance();
        tokenizer.advance();

        compileExpression();

        int remember = labelIndex;
        labelIndex ++ ;
        labelIndex ++ ;
        vmWriter.writeArithmetic(VMWriter.Command.NOT);
        vmWriter.writeIf("if-goto " + className+ "_" + (remember+1) );
       
        tokenizer.advance();
        tokenizer.advance();

        compileStatements();

        vmWriter.writeGoto("goto " + className+ "_" + (remember ));
        vmWriter.writeLabel("label " + className+ "_" + (remember+1 ));
       

        tokenizer.advance();
        
        if (tokenizer.currToken.equals("else")){

            tokenizer.advance();
            tokenizer.advance();
            compileStatements();
            tokenizer.advance();
        }
        vmWriter.writeLabel("label " + className+ "_" + remember );
    }
    public static void compileExpression() throws IOException {
       
        compileTerm();
        while(tokenizer.currToken.equals("+") || tokenizer.currToken.equals("-") || tokenizer.currToken.equals("*") || tokenizer.currToken.equals("/") || tokenizer.currToken.equals("&") || tokenizer.currToken.equals("|") || tokenizer.currToken.equals("<") ||tokenizer.currToken.equals(">") || tokenizer.currToken.equals("=")){
            String currOp = tokenizer.currToken;
            tokenizer.advance();
            compileTerm(); 
            switch (currOp) {
                case "+" :
                    vmWriter.writeArithmetic(VMWriter.Command.ADD);
                    break;
                case "*":
                    vmWriter.writeCall("Math.multiply" , 2);
                    break;
                case "-":
                vmWriter.writeArithmetic(VMWriter.Command.SUB);
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

            if( tokenizer.currToken.equals("(")){
                tokenizer.advance();
                compileExpression();
                tokenizer.advance();
            }

            else if(tokenizer.currToken.equals("~") || tokenizer.currToken.equals("-")) {
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

        else if ( tokenizer.tokenType().equals("IDENTIFIER") ) { 
            String temp = tokenizer.currToken;
            tokenizer.advance();

            if (tokenizer.currToken.equals("(")) {
                tempString += temp + "";
                tokenizer.advance();
                compileExpressionList();
                if (isObj){
                    numOfexp++;
                    isObj= false;
                }
                if (isObjLet) {
                    numOfexp++;
                    vmWriter.writePush(VMWriter.Segment.THIS, indexOfObject);
                    isObjLet = false;
                }
                vmWriter.writeCall(tempString, numOfexp);
                tokenizer.advance();
                tempString= "";
            }
            else if (tokenizer.currToken.equals("[")) {
                tokenizer.advance();
                compileExpression(); 
                vmWriter.writePush(VMWriter.Segment.valueOf(symbolTable.kindOf(temp)), symbolTable.indexOf(temp));
                vmWriter.writeArithmetic(VMWriter.Command.ADD);
                vmWriter.writePop(VMWriter.Segment.POINTER, 1);
                vmWriter.writePush(VMWriter.Segment.THAT, 0);
                tokenizer.advance();
            }
            else if (tokenizer.currToken.equals(".")) {
                if (symbolTable.methodLevelMap.containsKey(temp) || symbolTable.classLevelMap.containsKey(temp) ){
                    indexOfObject = symbolTable.indexOf(temp);
                    temp = symbolTable.typeOf(temp);
                    isObjLet =true;
                }
                tempString += temp;
                tempString += ".";
                tokenizer.advance();
                compileTerm();
                tempString= "";
            }
            else{
                vmWriter.writePush(VMWriter.Segment.valueOf(symbolTable.kindOf(temp)) , symbolTable.indexOf(temp));
            }
        }
    }
   
    public static int compileExpressionList() throws IOException{
        numOfexp = 0 ; 
        parenthesisCounter = 1 ;
        if (!tokenizer.currToken.equals(")")) {
            compileExpression();
            numOfexp ++ ;
        }

        while (parenthesisCounter > 0 ) {
            if (tokenizer.currToken.equals("(")) {
                parenthesisCounter++; // Increment for nested '('
            }
            else if (tokenizer.currToken.equals(")")) {
                parenthesisCounter--; // Decrement for closing ')'

                // Exit when the parenthesis count returns to zero
                if (parenthesisCounter == 0) {
                    break;
                }
            }
            else if (tokenizer.currToken.equals(",")) {
                tokenizer.advance();
                compileExpression();
                numOfexp ++ ;
            }
        }

        return numOfexp;
    }
}