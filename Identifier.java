
public class Identifier {
    
    public String name; 
    public String type; 
    public  String kind; 
    public  int runningIndex;
    
    
    public Identifier(String name , String type , String kind , int runningIndex){
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.runningIndex = runningIndex;
        
    }
   
    @Override
    public String toString() {
        return "Identifier{name='" + name + "', type='" + type + "', kind='" + kind + "', runningIndex=" + runningIndex + "}";
    }

    

}
