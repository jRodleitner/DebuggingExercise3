package deltadebugger;



import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

public class Execute {

    //execute pdflatex and extract the error
    public Error compileAndExtractError(String texFilePath)
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                "pdflatex",
                "-interaction=nonstopmode",
                texFilePath
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String line;

        Pattern errorPattern = Pattern.compile("^! (.+)");

        while ((line = reader.readLine()) != null) {

            Matcher matcher = errorPattern.matcher(line);

            if (matcher.find()) {
                process.waitFor();

                String error = matcher.group(1).trim();

                return new Error(error);
            }
        }

        process.waitFor();

        return null;
    }
}


