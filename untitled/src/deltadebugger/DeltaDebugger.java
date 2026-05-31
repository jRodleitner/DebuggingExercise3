
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


        List<String> current = new ArrayList<>(originalLines);

        // for loop that tries to remove one line after another
        for (int i = 0; i < current.size(); i++) {

            // protected lines are skipped
            if (protectedLines.contains(i + 1)) {
                continue;
            }

            // skip any empty lines
            if (current.get(i).isBlank()) {
                continue;
            }

            // if line is neither empty nor protected, try removing the line:

            // create candidate to test pdflatex compilation with modified file
            List<String> candidate =
                    new ArrayList<>(current);

            // replace with empty line
            candidate.set(i, "");

            Path tempFile = Files.createTempFile("ddmin", ".tex");
            Files.write(tempFile, candidate);

            Error error = null;
            try {
                error = tester.compileAndExtractError(tempFile.toString());
            } finally {
                Files.deleteIfExists(tempFile);
            }

            // if the error is the same we may keep the change else we simply continue with the current version
            if (targetError.equals(error)) {


                // keep reduction
                current = candidate;

            } else {

                //do nothing: reject reduction
            }

        }

        return current;
    }
}

