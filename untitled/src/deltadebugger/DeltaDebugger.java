
package deltadebugger;

import java.nio.file.*;
import java.util.*;

public class DeltaDebugger {

    private final Execute tester;

    public DeltaDebugger(Execute tester) {
        this.tester = tester;
    }

    public List<String> reduce(
            List<String> originalLines,
            Set<Integer> protectedLines,
            Error targetError
    ) throws Exception {

        // Current minimized version
        List<String> current =
                new ArrayList<>(originalLines);

        // Try removing every line one by one
        for (int i = 0; i < current.size(); i++) {

            // Never remove protected structural lines
            System.out.println("Current line = " + (i + 1) + " content: " + current.get(i));
            if (protectedLines.contains(i + 1)) {
                continue;
            }

            // Skip already removed lines
            if (current.get(i).isBlank()) {
                continue;
            }

            System.out.println("Trying to remove line " + (i +1) + current.get(i));

            // Create candidate version
            List<String> candidate =
                    new ArrayList<>(current);

            // Replace line with empty line
            // (preserves line numbers)
            candidate.set(i, "");

            Path tempFile = Files.createTempFile("ddmin", ".tex");
            Files.write(tempFile, candidate);

            Error error = null;
            try {
                error = tester.compileAndExtractError(tempFile.toString());
            } finally {
                // 1. Delete the actual .tex file from the AppData/temp folder
                Files.deleteIfExists(tempFile);

                // 2. Extract ONLY the file name (e.g., "ddmin12345.tex")
                String fileName = tempFile.getFileName().toString();
                // 3. Strip the extension (e.g., "ddmin12345")
                String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

                // 4. Delete the aux files from the Java project folder (current working directory)
                Files.deleteIfExists(Paths.get(baseName + ".log"));
                Files.deleteIfExists(Paths.get(baseName + ".aux"));
                Files.deleteIfExists(Paths.get(baseName + ".pdf"));
                Files.deleteIfExists(Paths.get(baseName + ".out"));
            }
            System.out.println(error);

            // Check whether same error still occurs
            if (targetError.equals(error)) {

                System.out.println(
                        "Successfully removed line "
                                + (i + 1) + current.get(i)
                );

                // Keep reduction
                current = candidate;

            } else {

                System.out.println(
                        "Could not remove line "
                                + (i + 1) + current.get(i)
                );
            }
        }

        return current;
    }
}

