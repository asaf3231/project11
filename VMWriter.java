
public class VMWriter {

    // Represents the possible memory segments in the VM language
    public enum Segment {
        CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
    }

    // Represents the possible arithmetic/logic commands
    public enum Command {
        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
    }

   
    public VMWriter(PrintWriter out) {
        // Skeleton - no implementation
    }

   
    public void writePush(Segment segment, int index) {
        // Skeleton - no implementation
    }

  
    public void writePop(Segment segment, int index) {
        // Skeleton - no implementation
    }

   
    public void writeArithmetic(Command command) {
        // Skeleton - no implementation
    }

   
    public void writeLabel(String label) {
        // Skeleton - no implementation
    }

   
    public void writeGoto(String label) {
        // Skeleton - no implementation
    }

    
    public void writeIf(String label) {
        // Skeleton - no implementation
    }

    
    public void writeCall(String name, int nArgs) {
        // Skeleton - no implementation
    }

    
    public void writeFunction(String name, int nVars) {
        // Skeleton - no implementation
    }

    
    public void writeReturn() {
        // Skeleton - no implementation
    }

   
    public void close() {
        // Skeleton - no implementation
    }
}