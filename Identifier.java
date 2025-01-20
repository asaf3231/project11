
public class Identifier {
    
    private static String name; 
    private static String type; 
    private static String kind; 
    public  int runningIndex;
    
    
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
    @Override
    public String toString() {
        return "Identifier{name='" + name + "', type='" + type + "', kind='" + kind + "', runningIndex=" + runningIndex + "}";
    }

    

}
