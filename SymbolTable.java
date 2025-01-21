import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    // Possible kinds of symbols in the table
    public static enum Kind {
        STATIC, THIS, ARGUMENT, LOCAL, NONE
    }
    private static int staticIndex;
    public static int fieldIndex;
    private static int argIndex;
    public static int varIndex;

    public static HashMap <String,Identifier> classLevelMap ;
    public static HashMap <String,Identifier> methodLevelMap ;


    public SymbolTable() {
        classLevelMap = new HashMap<>();
        methodLevelMap = new  HashMap<>();
        staticIndex = 0;
        fieldIndex  = 0;
        argIndex    = 0;
        varIndex    = 0;
    }

   
    public void reset() {
        methodLevelMap.clear();
        argIndex    = 0;
        varIndex    = 0;
    }

   
    public void define(String name, String type, Kind kind) {

        if (kind.equals(Kind.STATIC)) {
            if (!classLevelMap.containsKey(name)) { // Prevent duplicate definitions
                Identifier newMember = new Identifier(name, type, Kind.STATIC.toString(), staticIndex);
                classLevelMap.put(name, newMember);
                staticIndex++;
            }
        } else if (kind.equals(Kind.THIS)) {
            if (!classLevelMap.containsKey(name)) {
                Identifier newMember = new Identifier(name, type, "this", fieldIndex);
                classLevelMap.put(name, newMember);
                fieldIndex++;
            }
        } else if (kind.equals(Kind.ARGUMENT)) {
            if (!methodLevelMap.containsKey(name)) {
                Identifier newMember = new Identifier(name, type, Kind.ARGUMENT.toString(), argIndex);
                methodLevelMap.put(name, newMember);
                argIndex++;
            }
        } else if (kind.equals(Kind.LOCAL)) {
            if (!methodLevelMap.containsKey(name)) {
                Identifier newMember = new Identifier(name, type, Kind.LOCAL.toString(), varIndex);
                methodLevelMap.put(name, newMember);
                varIndex++;
            }
        }
    }
   
    public int varCount(Kind kind) {

        if (kind.equals(kind.STATIC)){
            return staticIndex;
        }
        if (kind.equals(Kind.THIS)){
            return fieldIndex;
        }
        if (kind.equals(Kind.ARGUMENT)){
            return argIndex;
        }
        if (kind.equals(Kind.LOCAL)){
            return varIndex;
        }

        return 0;
    }

   
    public String kindOf(String name) {

        if ( methodLevelMap.containsKey(name) ) {
            return methodLevelMap.get(name).kind;    
        }
        else if ( classLevelMap.containsKey(name) ) {
            return classLevelMap.get(name).kind.toUpperCase();    
        } 

        return Kind.NONE.toString();
    }

   
    public String typeOf(String name) {
        if ( methodLevelMap.containsKey(name) ) {
            return methodLevelMap.get(name).type;    
        }
        else if ( classLevelMap.containsKey(name) ) {
            return classLevelMap.get(name).type;    
        } 

        return Kind.NONE.toString();
    }

    public static int indexOf(String name) {
        if ( methodLevelMap.containsKey(name) ) {
            return methodLevelMap.get(name).runningIndex;    
        }
        else if ( classLevelMap.containsKey(name) ) {
            return classLevelMap.get(name).runningIndex;    
        } 
        return -1;
    }

    public void printSymbolTable() {
        System.out.println("Class Level Map:");
        for (String key : classLevelMap.keySet()) {
            Identifier identifier = classLevelMap.get(key);
            System.out.println("Name: " + key +
                    ", Type: " + identifier.type +
                    ", Kind: " + identifier.kind +
                    ", Index: " + identifier.runningIndex);
        } 
    
        System.out.println("\nMethod Level Map:");
        for (String key : methodLevelMap.keySet()) {
            Identifier identifier = methodLevelMap.get(key);
            System.out.println("Name: " + key +
                    ", Type: " + identifier.type +
                    ", Kind: " + identifier.kind +
                    ", Index: " + identifier.runningIndex);
        }
    }

    public void printLocalVariables() {
    System.out.println("Local Variables in Method Level Map:");
    for (Map.Entry<String, Identifier> entry : classLevelMap.entrySet()) {
        Identifier identifier = entry.getValue();
        if (identifier.kind.equals(Kind.THIS.toString())) {
            System.out.println("Name: " + identifier.name +
                               ", Type: " + identifier.type +
                               ", Kind: " + identifier.kind +
                               ", Index: " + identifier.runningIndex);
        }
    }
}
}