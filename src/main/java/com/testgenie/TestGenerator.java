package com.testgenie;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.testgenie.utils.StringUtil.*;

/**
 * TestGenerator is responsible for generating a unit test file for a given Java source file.
 * It uses the JavaFileParser utility to extract class information and builds a test class
 * with JUnit 5 annotations and Mockito-style mocking.
 */
public class TestGenerator {
    private static final String INDENT = "    ";
    private static final String TEST_ANNOTATION = "@Test";

    /**
     * Entry point to generate the test file.
     *
     * @param sourceFile The original Java file to generate test stubs for.
     * @param outputDir The output directory to write the test file to. This should be the output directory.
     */
    public void generateTestFile(File sourceFile, String outputDir, Set<String> flags) {
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

        // Parse class name from an input file
        Optional<String> classNameOpt = JavaFileParser.parseClassOrInterfaceName(sourceFile);
        if (classNameOpt.isEmpty()) {
            System.err.println("Could not determine class name for file: " + sourceFile.getName());
            return;
        }

        // Get class name and supporting method/field metadata
        String className = classNameOpt.get();
        String testClassName = className + "Test";
        List<MethodDeclaration> methods = JavaFileParser.parseMethods(sourceFile);
        List<FieldDeclaration> fields = JavaFileParser.parseFields(sourceFile);

        // Generate private @Mock fields for the test methods
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

        // Add @BeforeEach method to initialize mocks
        testContent.append(INDENT)
                .append("@BeforeEach\n")
                .append(INDENT)
                .append("void setUp() {\n")
                .append(INDENT).append(INDENT)
                .append("MockitoAnnotations.openMocks(this);\n")
                .append(INDENT)
                .append("}\n\n");

        // Iterate over public methods and generate condition-based test stubs
        for (MethodDeclaration method : methods) {
            // Only generate test methods for public methods
            if (!method.isPublic()) continue;

            // Store the method name, test method name and method body
            String methodName = method.getNameAsString();
            String testMethodName = "test" + capitalizeFirst(methodName);
            BlockStmt body = method.getBody().orElse(new BlockStmt());

            // Capture the conditional variables based on class information being parsed
            boolean hasNullCheck = body.toString().contains("== null") ||
                    body.findAll(MethodCallExpr.class).stream().anyMatch(m -> m.getNameAsString().equals("requireNonNull"));

            boolean throwsException = method.getThrownExceptions().isEmpty() ||
                    body.findAll(ThrowStmt.class).isEmpty();

            boolean hasConditional = body.findAll(IfStmt.class).isEmpty() || body.findAll(SwitchStmt.class).isEmpty();

            boolean usesOptional = body.findAll(MethodCallExpr.class).stream().anyMatch(m -> m.getScope().map(Object::toString).orElse("").contains("Optional"));

            boolean returnsBoolean = method.getType().asString().equals("boolean") ||
                    body.findAll(ReturnStmt.class).stream().anyMatch(r -> r.toString().contains("true") || r.toString().contains("false"));

            boolean changesState = body.findAll(AssignExpr.class).stream()
                    .anyMatch(a -> !a.getTarget().toString().contains("final"));

            // Create the test stubs for any of the conditions above that are true
            // Generate stub for null-check logic
            if (hasNullCheck && flags.contains("null")) {
                testContent.append(INDENT).append("@Test\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_nullCheck() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: verify null handling\n")
                        .append(INDENT).append(INDENT).append("assertThrows(NullPointerException.class, () -> {\n")
                        .append(INDENT).append(INDENT).append(INDENT).append(lowercaseFirst(className)).append(".").append(methodName).append("(null);\n")
                        .append(INDENT).append(INDENT).append("});\n")
                        .append(INDENT).append("}\n\n");
            }

            // Generate stub for exception-throwing methods
            if (throwsException && flags.contains("exceptions")) {
                testContent.append(INDENT).append(TEST_ANNOTATION + "\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_throwsException() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: verify exception thrown\n")
                        .append(INDENT).append(INDENT).append("assertThrows(Exception.class, () -> {\n")
                        .append(INDENT).append(INDENT).append(INDENT).append(lowercaseFirst(className)).append(".").append(methodName).append("(...);\n")
                        .append(INDENT).append(INDENT).append("});\n")
                        .append(INDENT).append("}\n\n");
            }

            // Generate stub for conditional logic (if/switch)
            if (hasConditional && flags.contains("conditionals")) {
                testContent.append(INDENT).append(TEST_ANNOTATION + "\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_conditionals() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: verify branching logic\n")
                        .append(INDENT).append(INDENT).append("// Example: assertEquals(...)\n")
                        .append(INDENT).append("}\n\n");
            }

            // Generate stub for Optional-returning methods
            if (usesOptional && flags.contains("optionals")) {
                testContent.append(INDENT).append(TEST_ANNOTATION + "\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_returnsOptional() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: test Optional presence/absence\n")
                        .append(INDENT).append(INDENT).append("Optional<?> result = ").append(lowercaseFirst(className)).append(".").append(methodName).append("(...);\n")
                        .append(INDENT).append(INDENT).append("assertTrue(result.isPresent());\n")
                        .append(INDENT).append("}\n\n");
            }

            // Generate stub for boolean-returning methods
            if (returnsBoolean && flags.contains("booleans")) {
                testContent.append(INDENT).append(TEST_ANNOTATION + "\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_returnsBoolean() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: test true/false conditions\n")
                        .append(INDENT).append(INDENT).append("boolean result = ").append(lowercaseFirst(className)).append(".").append(methodName).append("(...);\n")
                        .append(INDENT).append(INDENT).append("assertTrue(result);\n")
                        .append(INDENT).append("}\n\n");
            }

            // Generate stub for state-changing methods
            if (changesState && flags.contains("state")) {
                testContent.append(INDENT).append(TEST_ANNOTATION + "\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_changesState() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: verify side effects or state changes\n")
                        .append(INDENT).append(INDENT).append(lowercaseFirst(className)).append(".").append(methodName).append("(...);\n")
                        .append(INDENT).append(INDENT).append("// assertEquals(expected, ...)\n")
                        .append(INDENT).append("}\n\n");
            }
        }

        // Class the test class
        testContent.append("}");

        // Write the contents to the output file in the expected output directory
        try {
            File outDir = new File(outputDir);
            if (!outDir.exists()) outDir.mkdirs();

            File testFile = new File(outDir, testClassName + ".java");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
                writer.write(testContent.toString());
            }

            System.out.println("Generated test file: " + testFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing test file: " + e.getMessage());
        }
    }
}
