# TestGenie
TestGenie is a simple command-line tool that automatically generates JUnit 5 test cases by scanning and analyzing Java source files. 
It uses the JavaParser library to understand code structure. From there, my JavaFileParser and TestGenerator classes handle filtering the nodes and
generating the testcases.

---

## What It Does
TestGenie parses Java files by breaking them into an abstract syntax tree (AST), allowing it to analyze:

- Imports
- Classes and constructors
- Methods and arguments
- Internal logic and conditions

This structure makes it possible to evaluate each node and generate tests that match the behavior of the source code.

![Screenshot 2025-07-26 at 7.53.44 PM.png](src/main/resources/javaparser/images/Screenshot%202025-07-26%20at%207.53.44%E2%80%AFPM.png)

From here I am able to evaluate the different tree nodes to help write test cases for the individual Java files.

The bulk of the actual project logic is within JavaFileParser and TestGenerator classes under src/main/java/com/testgenie. 

## Entry Point
Most of the logic lives in:

- `src/main/java/com/testgenie/JavaFileParser.java`

- `src/main/java/com/testgenie/TestGenerator.java`

The CLI entry point is:

- `src/main/java/com/testgenie/App.java`

## Features

- Parses any .java file placed in the /samples directory
- Analyzes method bodies, arguments, conditionals, and exceptions
- Generates clean, compilable JUnit 5 tests in the /output directory
- Uses Picocli for CLI parsing—no runtime dependencies required
  
  | Null checks     | `if (arg == null)` or `Objects.requireNonNull`     |
  |-----------------|----------------------------------------------------|
  | Exceptions      | `throw new ...` or `assertThrows(...)`             |
  | Conditionals    | `if`, `switch`                                     |
  | Optional return | `Optional.of`, `Optional.empty()`                  |
  | Boolean return  | `return true/false` or comparison                  |
  | State change    | Non-`final` instance variables, e.g., `balance +=` |

## How to Run

### 1. Clone and Build
```bash
git clone https://github.com/yourname/testgenie.git
cd testgenie
./gradlew build
```
### 2. Run the Project
You can run the project on the samples in the samples folder by running the following command:
```bash
./gradlew run --args="samples/Calculator.java"
```
You can replace the args value with other java files other than Calculator. 
The test cases will get created under the **ouput** directory appended by Test. For example, the command above will generate the following filename in the output directory: **CalculatorTest.java**

### 3. Review the TestCase Output
For convenience, you can run the **cat** terminal command on the test file like so:
```bash
cat output/CalculatorTest.java
```
## Final Notes
You can add your own Java files to the /samples directory and run the generator on them.
Test output files are always named using the original class name with a Test suffix (e.g., Calculator → CalculatorTest.java).
