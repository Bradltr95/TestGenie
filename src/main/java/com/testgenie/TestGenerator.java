package com.testgenie;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.util.List;
import java.util.Optional;

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

        // Get the name of the Class we are writing test stubs for
        Optional<String> classNameOpt = JavaFileParser.parseClassOrInterfaceName(sourceFile);

        if (classNameOpt.isEmpty()) {
            System.err.println("Could not determine class name for file: " + sourceFile.getName());
            return;
        }
        // These variables hold the class name, methods, and fields we will create the stub tests for
        String className = classNameOpt.get();
        String testClassName = className + "Test";
        List<MethodDeclaration> methods = JavaFileParser.parseMethods(sourceFile);
        List<FieldDeclaration> fields = JavaFileParser.parseFields(sourceFile);

        // Generate mock fields
        for (FieldDeclaration field : fields) {
            if (field.isStatic()) continue;
            String fieldType = field.getVariable(0).getTypeAsString();
            String fieldName = field.getVariable(0).getNameAsString();
            testContent.append(INDENT)
                    .append("@Mock\n")
                    .append(INDENT)
                    .append("private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }

        // Add @InjectMocks for the class under test
        testContent.append(INDENT)
                .append("@InjectMocks\n")
                .append(INDENT)
                .append("private ").append(className).append(" ").append(lowercaseFirst(className)).append(";\n\n");

        // Add setup method
        testContent.append(INDENT)
                .append("@BeforeEach\n")
                .append(INDENT)
                .append("void setUp() {\n")
                .append(INDENT).append(INDENT)
                .append("MockitoAnnotations.openMocks(this);\n")
                .append(INDENT)
                .append("}\n\n");

        // Generate test method stubs
        for (MethodDeclaration method : methods) {
            if (method.isPublic()) {
                String methodName = method.getNameAsString();
                testContent.append(INDENT)
                        .append("@Test\n")
                        .append(INDENT)
                        .append("void test").append(capitalizeFirst(methodName)).append("() {\n")
                        .append(INDENT).append(INDENT)
                        .append("// TODO: implement test for ").append(methodName).append("\n")
                        .append(INDENT).append(INDENT)
                        .append("// Example: when(mock.someMethod()).thenReturn(value);\n")
                        .append(INDENT).append(INDENT)
                        .append("// assertEquals(expected, ").append(lowercaseFirst(className)).append(".").append(methodName).append("(...));\n")
                        .append(INDENT)
                        .append("}\n\n");
            }
        }

        testContent.append("}");
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
