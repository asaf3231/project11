import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {

    // Represents the possible memory segments in the VM language
    public static enum Segment {
        CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
    }
    // Represents the possible arithmetic/logic commands
    public static enum Command {
        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
    }
    private static BufferedWriter writer;

   
    public VMWriter(File outputFile) throws IOException{
        writer = new BufferedWriter(new FileWriter(outputFile));
    }

   
    public void writePush(Segment segment, int index)throws IOException {
        writer.write("push");
        writer.write(segment.toString().toLowerCase());
        writer.write(index);
    }

  
    public void writePop(Segment segment, int index) throws IOException{
        writer.write("pop");
        writer.write(segment.toString().toLowerCase());
        writer.write(index);    
    }

   
    public void writeArithmetic(Command command) throws IOException {
        writer.write(command.toString().toLowerCase());
    }

   
    public void writeLabel(String label)throws IOException {
        writer.write(label);
    }

   
    public void writeGoto(String label) throws IOException{
        writer.write(label);
    }

    
    public void writeIf(String label)throws IOException {
        writer.write(label);
    }

    
    public void writeCall(String name, int nArgs)throws IOException {
        writer.write("call " + name + nArgs);
    }

    
    public void writeFunction(String name, int nVars) throws IOException{
        writer.write("function " + name + nVars);

    }

    
    public void writeReturn() throws IOException{
        writer.write("return");
    }

   
    public void close() throws IOException{
        writer.close();
    }
}