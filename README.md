# TestGenie
A simple CLI tool that scans your Java methods and generates intelligent JUnit 5 test stubs — **fully offline** and no setup needed.

## Features

- Parses any Java class file in the /samples directory.
- Analyzes the methods and logic.
- Generates readable, compilable JUnit5 test cases in the output/ directory appended by Test.
- Runs locally within the CLI with no dependencies.

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
