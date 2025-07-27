package com.testgenie;

import picocli.CommandLine;

@CommandLine.Command(
        name = "testgenie",
        description = "Generates JUnit 5 test classes from Java source files.",
        mixinStandardHelpOptions = true
)
public class App {
    public static void main(String[] args) {
        // Standard outputs should not be used to directly log anything. This is just Stub code for now.
        System.out.println("Test Genie App");
    }
}
