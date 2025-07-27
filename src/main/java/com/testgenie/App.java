package com.testgenie;

import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "testgenie",
        description = "Generates JUnit 5 test classes from Java source files.",
        mixinStandardHelpOptions = true
)
public class App implements Callable<Integer> {
    @CommandLine.Option(names = {"-i", "--input"}, description = "Path to Java source file", required = true)
    private File inputFile;

    public static void main(String[] args) {
        // Standard outputs should not be used to directly log anything. This is just Stub code for now.
        System.out.println("Test Genie App");
    }

    @Override
    public Integer call() {
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("Invalid input file: " + inputFile);
            return 1;
        }

        return 0;
    }
}
