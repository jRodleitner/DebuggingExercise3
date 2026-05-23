
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

            // Write temporary test file
            Path tempFile =
                    Files.createTempFile("ddmin", ".tex");

            Files.write(tempFile, candidate);

            // Compile candidate
            Error error =
                    tester.compileAndExtractError(
                            tempFile.toString()
                    );

            // Delete temporary file
            Files.delete(tempFile);

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

