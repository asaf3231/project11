
public class Identifier {
    
    
    private static String name; 
    private static String type; 
    private static String kind; 
    private static int runningIndex;
    
    
    public Identifier(String name , String type , String kind , int runningIndex){
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.runningIndex = runningIndex;
        
    }

    public static String getName() {
        return name;
    }
    public static String getType() {
        return type;
    }
    public static String getKind() {
        return kind;
    }
    public static int getRunningIndex() {
        return runningIndex;
    }

}
