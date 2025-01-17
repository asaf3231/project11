import java.util.HashMap;

public class SymbolTable {

    // Possible kinds of symbols in the table
    public static enum Kind {
        STATIC, THIS, ARG, LOCAL, NONE
    }
    private static int staticIndex;
    private static int fieldIndex;
    private static int argIndex;
    private static int varIndex;

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
        
        staticIndex = 0;
        fieldIndex  = 0;
        argIndex    = 0;
        varIndex    = 0;
    }

   
    public void define(String name, String type, Kind kind) {
        
        if (kind.equals(Kind.STATIC)){
            Identifier newMember = new Identifier(name, type, Kind.STATIC.toString(), staticIndex);
            classLevelMap.put(name, newMember);
            staticIndex ++ ;
            System.out.println("staticcccccc");
            System.out.println(newMember.getName());
            System.out.println(newMember.getType());
            System.out.println(newMember.getKind());
            System.out.println(newMember.getRunningIndex());
        }
        else if (kind.equals(Kind.THIS) ){
            Identifier newMember = new Identifier(name, type, "this", fieldIndex);
            classLevelMap.put(name, newMember);
            fieldIndex ++; 
            System.out.println("thissssss");
            System.out.println(newMember.getName());
            System.out.println(newMember.getType());
            System.out.println(newMember.getKind());
            System.out.println(newMember.getRunningIndex());
        }
        else if (kind.equals(Kind.ARG) ){
            Identifier newMember = new Identifier(name, type, Kind.ARG.toString(), argIndex);
            methodLevelMap.put(name, newMember);
            argIndex ++; 
            System.out.println("arggggg");
            System.out.println(newMember.getName());
            System.out.println(newMember.getType());
            System.out.println(newMember.getKind());
            System.out.println(newMember.getRunningIndex());
        }
        else if (kind.equals(Kind.LOCAL)){
            Identifier newMember = new Identifier(name, type, Kind.LOCAL.toString(), varIndex);
            methodLevelMap.put(name, newMember);
            varIndex ++; 
            System.out.println("locallllll");
            System.out.println(newMember.getName());
            System.out.println(newMember.getType());
            System.out.println(newMember.getKind());
            System.out.println(newMember.getRunningIndex());
        }
    }

   
    public int varCount(Kind kind) {

        if (kind.equals(kind.STATIC)){
            return staticIndex;
        }
        if (kind.equals(Kind.THIS)){
            return fieldIndex;
        }
        if (kind.equals(Kind.ARG)){
            return argIndex;
        }
        if (kind.equals(Kind.LOCAL)){
            return varIndex;
        }

        return 0;
    }

   
    public String kindOf(String name) {

        if ( methodLevelMap.containsKey(name) ) {
            return methodLevelMap.get(name).getKind();    
        }
        else if ( classLevelMap.containsKey(name) ) {
            return classLevelMap.get(name).getKind();    
        } 

        return Kind.NONE.toString();
    }

   
    public String typeOf(String name) {
        if ( methodLevelMap.containsKey(name) ) {
            return methodLevelMap.get(name).getType();    
        }
        else if ( classLevelMap.containsKey(name) ) {
            return classLevelMap.get(name).getType();    
        } 

        return Kind.NONE.toString();
    }

    public int indexOf(String name) {
        if ( methodLevelMap.containsKey(name) ) {
            return methodLevelMap.get(name).getRunningIndex();    
        }
        else if ( classLevelMap.containsKey(name) ) {
            return classLevelMap.get(name).getRunningIndex();    
        } 
        return -1;
    }

    public void printSymbolTable() {
        System.out.println("Class Level Map:");
        for (String key : classLevelMap.keySet()) {
            Identifier identifier = classLevelMap.get(key);
            System.out.println("Name: " + key +
                    ", Type: " + identifier.getType() +
                    ", Kind: " + identifier.getKind() +
                    ", Index: " + identifier.getRunningIndex());
        } 
    
        System.out.println("\nMethod Level Map:");
        for (String key : methodLevelMap.keySet()) {
            Identifier identifier = methodLevelMap.get(key);
            System.out.println("Name: " + key +
                    ", Type: " + identifier.getType() +
                    ", Kind: " + identifier.getKind() +
                    ", Index: " + identifier.getRunningIndex());
        }
    }
}