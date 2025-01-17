import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Compilation {

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
    private static VMWriter vmWriter;


    public Compilation (File inputFile, File outputFile) throws IOException{
        reader = new BufferedReader(new FileReader(inputFile));
        writer = new BufferedWriter(new FileWriter(outputFile));
        tokenizer = new JackTokenizer(inputFile);
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter(outputFile);
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
    
    public static void compileParamaterList() throws IOException {
    
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
           complieVarDec();
        }
        compileStatements();
        tokenizer.advance();

    }

   public static void complieVarDec() throws IOException {
       
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
   
    public static void compileStatements() throws IOException{
        // Skeleton - no implementation
    }
   
    public static void compileDo() throws IOException{
        // Skeleton - no implementation
    }
    
    public static void compileLet() throws IOException{
        // Skeleton - no implementation
    }
    public static void compileWhile() throws IOException{
        // Skeleton - no implementation
    }
  
    public static void compileReturn() throws IOException{
        // Skeleton - no implementation
    }
   
    public static void compileIf() throws IOException{
        // Skeleton - no implementation
    }
    
    public static void compileExpression() throws IOException{
        // Skeleton - no implementation
    }
  
    public static void compileTerm() throws IOException{
        // Skeleton - no implementation
    }
   
    public static int compileExpressionList() throws IOException{
        // Skeleton - no implementation
        return 0;
    }
}