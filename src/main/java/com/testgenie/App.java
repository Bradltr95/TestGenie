package com.testgenie;

import picocli.CommandLine;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandLine.Command(
        name = "testgenie",
        description = "Generates JUnit 5 test classes from Java source files.",
        mixinStandardHelpOptions = true
)
public class App implements Callable<Integer> {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    @CommandLine.Option(names = {"-i", "--input"}, description = "Path to Java source file", required = true)
    private File inputFile;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output directory for generated test class")
    private String outputDir = "output";

    @CommandLine.Option(
            names = {"-f", "--flag"},
            description = "Flags to filter which test stubs are generated",
            split = ","
    )
    private final Set<String> flags = new HashSet<>();

    @CommandLine.Option(
            names = {"--ignore"},
            description = "Flags to ignore which test stubs are generated",
            split = ","
    )
    private final Set<String> ignore = new HashSet<>();

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        final int ERROR = 1;
        final int SUCCESS = 0;

        if (!inputFile.exists() || !inputFile.isFile()) {
            logger.log(Level.SEVERE, "Invalid input file: {0}", inputFile);
            return ERROR;
        }

        TestGenerator generator = new TestGenerator(inputFile, outputDir, flags, ignore);

        try {
            // This method will generate the test class and write the file to outputDir
            generator.generateImports();
            generator.generateTestFile();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to generate test file: {0}", e.getMessage());
            return ERROR;
        }

        return SUCCESS;
    }
}
