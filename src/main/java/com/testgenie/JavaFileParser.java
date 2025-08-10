package com.testgenie;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFileParser is responsible for:
 * - Parsing Java source files using JavaParser.
 * @parseMethod Extracts all method declarations from the source code.
 * @parseFields Extracting all method fields from the source code.
 * @parseClassOrInterfaceName Extracts the class or interface name from the source code.
 *
 * This class uses StaticJavaParser for simplicity, which provides a static entry point
 * to parse files without explicitly creating a JavaParser configuration.
 */
public class JavaFileParser {
    private static final Logger logger = Logger.getLogger(JavaFileParser.class.getName());

    /**
     * Parses a given Java file and extracts all method declarations.
     *
     * @param file The Java source file to parse.
     * @return A list of MethodDeclaration nodes found in the file.
     *         Returns an empty list if parsing fails or no methods are found.
     */
    public static List<MethodDeclaration> parseMethods(File file) {
        try {
            // Parse the Java file into a CompilationUnit (AST root)
            CompilationUnit compUnit = StaticJavaParser.parse(file);

            // Find all method declarations in the file and return them
            return compUnit.findAll(MethodDeclaration.class)
                    .stream()
                    .toList();

        } catch(Exception e) {
            // If parsing fails, return an empty list instead of throwing and return an error message
            logger.log(Level.SEVERE, "Failed to parse file for methods: {0}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Parses a given Java file and extracts all field declarations.
     *
     * @param file The Java source file to parse.
     * @return A list of FieldDeclaration nodes found in the file.
     *         Returns an empty list if parsing fails or no fields are found.
     */
    public static List<FieldDeclaration> parseFields(File file) {
        try {
            CompilationUnit compUnit = StaticJavaParser.parse(file);

            return compUnit.findAll(FieldDeclaration.class)
                    .stream()
                    .toList();
        } catch(Exception e) {
            // If parsing fails, return an empty list instead of throwing and return an error message
            logger.log(Level.SEVERE, "Failed to parse file for class fields: {0}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Parses a given Java file and extracts the class or interface name.
     * This method is used to capture the name of the file and prefixing it with Test during the TestGeneration.
     *
     * @param file The Java source file to parse.
     * @return A list of ClassOrInterfaceDeclaration nodes found in the file.
     *      Returns an empty list if parsing fails or no fields are found.
     */
    public static Optional<String> parseClassOrInterfaceName(File file) {
        try {
            CompilationUnit compUnit = StaticJavaParser.parse(file);

            return compUnit.findAll(ClassOrInterfaceDeclaration.class)
                    .stream()
                    .findFirst()
                    .map(ClassOrInterfaceDeclaration::getNameAsString);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Failed to parse file for ClassOrInterfanceName: {0}", e.getMessage());
            return Optional.empty();
        }
    }
}
