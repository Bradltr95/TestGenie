package com.testgenie;

import java.io.File;

public class TestGenerator {
    private static final String INDENT = "    ";

    /**
     * Entry point to generate the test file.
     *
     * @param sourceFile The original Java file to generate test stubs for.
     * @param outputDir The output directory to write the test file to. This should be the output directory.
     */
    public void generateTestFile(File sourceFile, String outputDir) {
        StringBuilder testContent = new StringBuilder();
        testContent.append("package com.testgenie.generated;\n\n")
                .append("import org.junit.jupiter.api.BeforeEach;\n")
                .append("import org.junit.jupiter.api.Test;\n")
                .append("import org.mockito.InjectMocks;\n")
                .append("import org.mockito.Mock;\n")
                .append("import org.mockito.MockitoAnnotations;\n")
                .append("\n")
                .append("import static org.mockito.Mockito.*;\n")
                .append("import static org.junit.jupiter.api.Assertions.*;\n\n");
    }

    /**
     * Lowercases the first character of a given string.
     */
    private String lowercaseFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Capitalizes the first character of a given string.
     */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
