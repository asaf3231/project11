import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class JackAnalyzer {

    public static void main(String[] args) throws IOException {

        // 1) Basic argument check:
        if (args.length == 0) {
            System.out.println("Usage: java JackAnalyzer <inputFileOrDirectory>");
            return;
        }
        // 2) Determine if 'source' is a file or a directory
        File source = new File(args[0]);
        
        if (!source.exists()) {
            System.out.println("Error: The specified path does not exist.");
            return;
        }

        if (source.isDirectory()) {
            // 3) Handle directory: find all .jack files in this directory
            File[] jackFiles = source.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jack");
                }
            });

            if (jackFiles == null || jackFiles.length == 0) {
                System.out.println("No .jack files found in directory: " + source.getAbsolutePath());
                return;
            }

            // 4) For each .jack file, create a corresponding .xml output and compile
            for (File jackFile : jackFiles) {
                String outFileName = jackFile.getAbsolutePath().replace(".jack", ".xml");
                File outFile = new File(outFileName);
                System.out.println("Compiling " + jackFile.getName() + " -> " + outFile.getName());
                new CompilationEngine(jackFile, outFile);
            }
        } else {
            // 5) Handle single file
            //    Ensure the file name ends with .jack
            if (!source.getName().toLowerCase().endsWith(".jack")) {
                System.out.println("Error: Input file must have a .jack extension.");
                return;
            }
            String outFileName = source.getAbsolutePath().replace(".jack", ".xml");
            File outFile = new File(outFileName);
            System.out.println("Compiling " + source.getName() + " -> " + outFile.getName());
            new CompilationEngine(source, outFile);
        }
    }
}