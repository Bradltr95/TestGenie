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

import static com.testgenie.utils.StringUtil.*;

/**
 * TestGenerator is responsible for generating a unit test file for a given Java source file.
 * It uses the JavaFileParser utility to extract class information and builds a test class
 * with JUnit 5 annotations and Mockito-style mocking.
 */
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
            // Only generate test methods for public methods
            if (!method.isPublic()) continue;

            // Store the method name, test method name and method body
            String methodName = method.getNameAsString();
            String testMethodName = "test" + capitalizeFirst(methodName);
            BlockStmt body = method.getBody().orElse(new BlockStmt());

            boolean hasNullCheck = body.toString().contains("== null") ||
                    body.findAll(MethodCallExpr.class).stream().anyMatch(m -> m.getNameAsString().equals("requireNonNull"));
            boolean throwsException = method.getThrownExceptions().size() > 0 ||
                    body.findAll(ThrowStmt.class).size() > 0;
            boolean hasConditional = body.findAll(IfStmt.class).size() > 0 || body.findAll(SwitchStmt.class).size() > 0;
            boolean usesOptional = body.findAll(MethodCallExpr.class).stream().anyMatch(m -> m.getScope().map(Object::toString).orElse("").contains("Optional"));
            boolean returnsBoolean = method.getType().asString().equals("boolean") ||
                    body.findAll(ReturnStmt.class).stream().anyMatch(r -> r.toString().contains("true") || r.toString().contains("false"));
            boolean changesState = body.findAll(AssignExpr.class).stream()
                    .anyMatch(a -> !a.getTarget().toString().contains("final"));

            if (hasNullCheck) {
                testContent.append(INDENT).append("@Test\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_nullCheck() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: verify null handling\n")
                        .append(INDENT).append(INDENT).append("assertThrows(NullPointerException.class, () -> {\n")
                        .append(INDENT).append(INDENT).append(INDENT).append(lowercaseFirst(className)).append(".").append(methodName).append("(null);\n")
                        .append(INDENT).append(INDENT).append("});\n")
                        .append(INDENT).append("}\n\n");
            }

            if (throwsException) {
                testContent.append(INDENT).append("@Test\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_throwsException() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: verify exception thrown\n")
                        .append(INDENT).append(INDENT).append("assertThrows(Exception.class, () -> {\n")
                        .append(INDENT).append(INDENT).append(INDENT).append(lowercaseFirst(className)).append(".").append(methodName).append("(...);\n")
                        .append(INDENT).append(INDENT).append("});\n")
                        .append(INDENT).append("}\n\n");
            }

            if (hasConditional) {
                testContent.append(INDENT).append("@Test\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_conditionals() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: verify branching logic\n")
                        .append(INDENT).append(INDENT).append("// Example: assertEquals(...)\n")
                        .append(INDENT).append("}\n\n");
            }

            if (usesOptional) {
                testContent.append(INDENT).append("@Test\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_returnsOptional() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: test Optional presence/absence\n")
                        .append(INDENT).append(INDENT).append("Optional<?> result = ").append(lowercaseFirst(className)).append(".").append(methodName).append("(...);\n")
                        .append(INDENT).append(INDENT).append("assertTrue(result.isPresent());\n")
                        .append(INDENT).append("}\n\n");
            }

            if (returnsBoolean) {
                testContent.append(INDENT).append("@Test\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_returnsBoolean() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: test true/false conditions\n")
                        .append(INDENT).append(INDENT).append("boolean result = ").append(lowercaseFirst(className)).append(".").append(methodName).append("(...);\n")
                        .append(INDENT).append(INDENT).append("assertTrue(result);\n")
                        .append(INDENT).append("}\n\n");
            }

            if (changesState) {
                testContent.append(INDENT).append("@Test\n")
                        .append(INDENT).append("void ").append(testMethodName).append("_changesState() {\n")
                        .append(INDENT).append(INDENT).append("// TODO: verify side effects or state changes\n")
                        .append(INDENT).append(INDENT).append(lowercaseFirst(className)).append(".").append(methodName).append("(...);\n")
                        .append(INDENT).append(INDENT).append("// assertEquals(expected, ...)\n")
                        .append(INDENT).append("}\n\n");
            }
        }

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
