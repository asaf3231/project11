public class SymbolTable {

    // Possible kinds of symbols in the table
    public enum Kind {
        STATIC, FIELD, ARG, VAR, NONE
    }

    public SymbolTable() {
        // No implementation needed here for the API skeleton
    }

   
    public void reset() {
        // No implementation needed here for the API skeleton
    }

   
    public void define(String name, String type, Kind kind) {
        // No implementation needed here for the API skeleton
    }

   
    public int varCount(Kind kind) {
        // No implementation needed here for the API skeleton
        return 0;
    }

   
    public Kind kindOf(String name) {
        // No implementation needed here for the API skeleton
        return Kind.NONE;
    }

   
    public String typeOf(String name) {
        // No implementation needed here for the API skeleton
        return null;
    }

    public int indexOf(String name) {
        // No implementation needed here for the API skeleton
        return -1;
    }
}