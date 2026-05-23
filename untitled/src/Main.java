
import deltadebugger.*;
import deltadebugger.Error;

import java.nio.file.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String inputFile = "C:\\Users\\jonar\\Dev\\GitHub\\DebuggingExercise3\\untitled\\src\\texfiles\\broken_2.tex";

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
                debugger.minimize(
                        lines,
                        protectedLines,
                        originalError
                );
        System.out.println("Test: " + minimized);
        Path output =
                Paths.get("C:\\Users\\jonar\\Dev\\GitHub\\DebuggingExercise3\\untitled\\src\\minimized\\minimized.tex");

        Files.write(output, minimized);

        System.out.println("Minimized file written to:");
        System.out.println(output);
    }
}


