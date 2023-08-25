# JavaScript Interpreter
This program is a JavaScripty (Somewhat simplified although almost totally functional JavaScript variant) Interpreter written in Scala. It was written as a project for a programming languages class. As the architecture of the interpreter is somewhat complex I will explain here how it works and in what files different parts of the implementation can be found. Another good place to look to understand this program better is in the lab4.pdf file in the root of the project. This file is the assignment and tells what part of the interpreter was built in this section of the lab. Note that sections that aren't in the lab4.pdf were built in earlier parts of the course (i.e. labs 1-3)




# JavaScript Interpreter in Scala

## High-Level Overview

The JavaScript Interpreter in Scala is a sophisticated project that aims to parse and execute JavaScript code using Scala as the underlying technology. Some of the code was provided as boilerplate or skeleton code, but the primary logic resides in `Lab4.scala` which was written entirely by a partner and I. The project is designed with a modular architecture, incorporating various components such as parsers, abstract syntax trees (AST), and evaluators to facilitate the interpretation process.

### How It's Implemented

The core of the interpreter is built around a recursive descent parser, which generates an AST for the JavaScript code. The AST is then traversed by an evaluator that executes the code in a simulated environment. The project leverages Scala's functional programming paradigms, including pattern matching and higher-order functions, to achieve a clean and efficient implementation.

State management is handled through monads, encapsulated in utility files like `DoWith.scala` and `DoWithContext.scala`. These utilities ensure that the interpreter maintains the correct state and context throughout the execution.

## Key Files

### Source Code

- **`src/main/scala/jsy/lab4/ast.scala`**: Defines the Abstract Syntax Tree (AST) for representing JavaScript code.
- **`src/main/scala/jsy/lab4/Parser.scala`**: Contains the parser that converts JavaScript code into an AST.
- **`src/main/scala/jsy/student/Lab4.scala`**: The main implementation file where most of the evaluation logic resides.

### Utilities

- **`src/main/scala/jsy/util/DoWith.scala`**: A utility for handling state in a monadic fashion.
- **`src/main/scala/jsy/util/DoWithContext.scala`**: Similar to `DoWith.scala`, but tailored for context management.
- **`src/main/scala/jsy/util/JsyApplication.scala`**: Utility for running the application.

### Tests

- **`src/test/scala/jsy/student/Lab4Spec.scala`**: Contains test specifications for the interpreter.
- **`src/test/scala/jsy/tester/JavascriptyTester.scala`**: A tester utility for running the tests.

## How to Test

1. **Clone the Repository**: Clone the GitHub repository to your local machine.
2. **Navigate to the Project Directory**: Open a terminal and navigate to the project directory.
3. **Run Tests**: Execute the following command to run the tests:
    ```
    sbt test
    ```
4. **Check Results**: After running the tests, you'll see the results in the terminal. This will indicate whether the interpreter is working as expected.

5. **Custom Tests**: You can also add your own tests in the `src/test/resources/lab4/` directory. Make sure to follow the existing naming conventions for test files.

By following these steps, you can ensure that the JavaScript Interpreter in Scala is functioning correctly and is ready for further development or deployment.


# Parsing of Javascripty
The parsing of JavaScripty into AST (Abstract Syntax Trees), which our interpreter can understand is done in the file ./src/main/scala/jsy/lab4/Parser.scala. You can read through the implementation of this to understand it better but I will give an example of the inputs and outputs here. An example JavaScripty input might look like this. This declares a function that takes in x and y and returns x. Then this function is called with (4,true)

``` function (x: number, y: bool) { return x} (4, true) ```

After going through the parser this function will be turned into the following AST to be evaluated by the interpreter. 


```  Call(Function(None,List((x,MTyp(MConst,TNumber)),(y,MTyp(MConst,TBool))),None,Var(x)),List(N(4.0), B(true))) ```

# Step Function
The evaluation of the AST into a value, or the evaluation of our program in other terms, is done in the step function. This function can be found at the location src/main/scala/jsy/student/Lab4.scala. Here the AST is simplified down step by step until it returns a value. The interpreter uses functional programming concepts in that it expects to evaluate every expression down to a value. In the given example before, the AST would eventually be evaluated down to the value ``` N(4.0) ``` to represent the value the function would return of the number 4. 
