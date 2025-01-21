import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {

    // Represents the possible memory segments in the VM language
    public static enum Segment {
        CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP ,NONE
    }
    // Represents the possible arithmetic/logic commands
    public static enum Command {
        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
    }
    public  static BufferedWriter writer;

   
    public VMWriter(File outputFile) throws IOException{
        writer = new BufferedWriter(new FileWriter(outputFile));
    }

   
    public void writePush(Segment segment, int index)throws IOException {

            writer.write("    " +"push ");
            writer.write(segment.toString().toLowerCase());
            writer.write(" ");
            writer.write(index + "");
            writer.newLine();   
    }

  
    public void writePop(Segment segment, int index) throws IOException{
        writer.write("    " + "pop ");
        writer.write(segment.toString().toLowerCase());
        writer.write(" ");
        writer.write(index + "");    
        writer.newLine();

    }

   
    public void writeArithmetic(Command command) throws IOException {
        writer.write("    " + command.toString().toLowerCase());
        writer.newLine();
    }

   
    public void writeLabel(String label)throws IOException {
        writer.write(label);
        writer.newLine();

    }

   
    public void writeGoto(String label) throws IOException{
        writer.write("    " + label);
        writer.newLine();

    }

    
    public void writeIf(String label)throws IOException {
        writer.write("    " + label);
        writer.newLine();
    }

    
    public void writeCall(String name, int nArgs)throws IOException {
        writer.write("    " + "call " + name + " " +nArgs);
        writer.newLine();

    }

    
    public void writeFunction(String name, int nVars) throws IOException{
        writer.write("function " + name + " " + nVars);
        writer.newLine();
    }

    
    public void writeReturn() throws IOException{
        writer.write("    " + "return\n");
    }

   
    public void close() throws IOException{
        writer.close();
        writer.newLine();
    }
}