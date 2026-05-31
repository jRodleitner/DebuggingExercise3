package deltadebugger;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SimpleParser {

    public static List<String> readLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path));
    }

    public static Set<Integer> getProtectedLines(List<String> lines) {
        Set<Integer> protectedLines = new HashSet<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            //Extract all lines that, are absolutely necessary for LaTex
            // e.g. They will, when removed, definitely change the error
            if (line.startsWith("\\documentclass")
                    || line.startsWith("\\begin{")
                    || line.startsWith("\\end{")
                    || line.startsWith("\\usepackage")) {

                protectedLines.add(i + 1);
            }
        }

        return protectedLines;
    }
}

