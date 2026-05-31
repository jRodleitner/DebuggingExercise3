
import deltadebugger.*;
import deltadebugger.Error;

import java.nio.file.*;
import java.util.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("**********************************************************");
        System.out.println("*********Welcome to Line by Line Delta-Debugger***********");
        System.out.println("**********************************************************");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the TeX filename (e.g., broken_1.tex) or type 'q' to exit: ");
            String filename = scanner.nextLine().trim();

            if (filename.equals("q")) {
                System.out.println("Thank you for using Line By Line Delta Debugger");
                break;
            }

            String inputFile = "src/texfiles/" + filename;

            try {
                List<String> lines = SimpleParser.readLines(inputFile);

                Set<Integer> protectedLines = SimpleParser.getProtectedLines(lines);

                Execute tester = new Execute();

                Error originalError =
                        tester.compileAndExtractError(inputFile);

                if (originalError == null) {
                    System.out.println("No LaTeX error found.");
                    deleteTempFiles();
                    System.out.println("----------------------------------------------------------");
                    continue;
                }

                DeltaDebugger debugger =
                        new DeltaDebugger(tester);

                List<String> minimized =
                        debugger.reduce(
                                lines,
                                protectedLines,
                                originalError
                        );

                //generate file that corresponds to the given input file
                Path output = Paths.get("src/minimized/minimized_" + filename);

                Files.write(output, minimized);

                System.out.print("Minimized file written to: ");
                System.out.println(output);

            } catch (Exception e) {
                System.err.println("An error occurred while processing " + filename + ": " + e.getMessage());
            } finally {
                deleteTempFiles();
                System.out.println("----------------------------------------------------------");
            }
        }

        scanner.close();
    }


    //Clean up all the .aux .log .pdf files that are generated during debugging
    private static void deleteTempFiles() {
        Path projectRoot = Paths.get(".");

        try (Stream<Path> files = Files.list(projectRoot)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> {
                        String filename = path.getFileName().toString().toLowerCase();
                        return filename.endsWith(".aux") ||
                                filename.endsWith(".log") ||
                                filename.endsWith(".pdf");
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete " + path.getFileName() + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Could not read project directory: " + e.getMessage());
        }
    }
}


