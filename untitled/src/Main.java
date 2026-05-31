
import deltadebugger.*;
import deltadebugger.Error;

import java.nio.file.*;
import java.util.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the TeX filename (e.g., broken_1.tex): ");
        String filename = scanner.nextLine().trim();

        String inputFile = "src/texfiles/" + filename;

        List<String> lines = SimpleParser.readLines(inputFile);

        Set<Integer> protectedLines = SimpleParser.getProtectedLines(lines);
        System.out.println("Protected Lines: " + protectedLines);
        Execute tester = new Execute();

        Error originalError =
                tester.compileAndExtractError(inputFile);


        if (originalError == null) {
            System.out.println("No LaTeX error found.");
            return;
        }

        System.out.println("Original error:");
        System.out.println(originalError);

        DeltaDebugger debugger =
                new DeltaDebugger(tester);

        List<String> minimized =
                debugger.reduce(
                        lines,
                        protectedLines,
                        originalError
                );
        System.out.println("Test: " + minimized);
        Path output = Paths.get("src/minimized/minimized_" + filename);
        scanner.close();

        Files.write(output, minimized);

        System.out.println("Minimized file written to:");
        System.out.println(output);

        deleteTempFiles();
    }

    public static void deleteTempFiles() {
        // Defines the path to the current project root directory
        Path projectRoot = Paths.get(".");

        System.out.println("Cleaning up temporary LaTeX files...");

        // Open a stream to read the files in the project root directory
        try (Stream<Path> files = Files.list(projectRoot)) {
            files.filter(Files::isRegularFile) // Ensure we only look at files, not folders
                    .filter(path -> {
                        String filename = path.getFileName().toString().toLowerCase();
                        return filename.endsWith(".aux") ||
                                filename.endsWith(".log") ||
                                filename.endsWith(".pdf");
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("Deleted: " + path.getFileName());
                        } catch (IOException e) {
                            System.err.println("Failed to delete " + path.getFileName() + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Could not read project directory: " + e.getMessage());
        }
    }
}


