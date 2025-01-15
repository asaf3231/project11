public class Main {
    public static void main(String[] args) {
        // 1) Basic argument check
        if (args.length == 0) {
            System.out.println("Usage: java Main <inputFileOrDirectory>");
            return;
        }
        // 2) Forward arguments to JackAnalyzer
        try {
            JackAnalyzer.main(args);
        } catch (Exception e) {
            System.out.println("An error occurred while running the JackAnalyzer.");
            e.printStackTrace();
        }
    }
}