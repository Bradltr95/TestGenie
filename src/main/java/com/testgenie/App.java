package com.testgenie;

import picocli.CommandLine;

import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "testgenie",
        description = "Generates JUnit 5 test classes from Java source files.",
        mixinStandardHelpOptions = true
)
public class App implements Callable<Integer> {
    @CommandLine.Option(names = {"-i", "--input"}, description = "Path to Java source file", required = true)
    private File inputFile;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output directory for generated test class")
    private String outputDir = "output";

    @CommandLine.Option(names = {"-f", "--flag"}, description = "Flags to filter which test stubs are generated")
    private Set<String> flags;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("Invalid input file: " + inputFile);
            return 1;
        }

        TestGenerator generator = new TestGenerator();

        try {
            // This method will generate the test class and write the file to outputDir
            generator.generateTestFile(inputFile, outputDir,flags);
        } catch (Exception e) {
            System.err.println("Failed to generate test file: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
