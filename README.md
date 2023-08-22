# JavaScript Interpreter
This program is a JavaScripty (Somewhat simplified although almost totally functional JavaScript variant) Interpreter written in Scala. It was written as a project for a programming languages class. As the architecture of the interpreter is somewhat complex I will explain here how it works and in what files different parts of the implementation can be found. Another good place to look to understand this program better is in the lab4.pdf file in the root of the project. This file is the assignment and tells what part of the interpreter was built in this section of the lab. Note that sections that aren't in the lab4.pdf were built in earlier parts of the course (i.e. labs 1-3)

# Parsing of Javascripty
The parsing of JavaScripty into AST (Abstract Syntax Trees), which our interpreter can understand is done in the file ./src/main/scala/jsy/lab4/Parser.scala. You can read through the implementation of this to understand it better but I will give an example of the inputs and outputs here. An example JavaScripty input might look like this. This declares a function that takes in x and y and returns x. Then this function is called with (4,true)

``` function (x: number, y: bool) { return x} (4, true) ```

After going through the parser this function will be turned into the following AST to be evaluated by the interpreter. 


```  Call(Function(None,List((x,MTyp(MConst,TNumber)),(y,MTyp(MConst,TBool))),None,Var(x)),List(N(4.0), B(true))) ```

# Step Function
The evaluation of the AST into a value, or the evaluation of our program in other terms, is done in the step function. This function can be found at the location src/main/scala/jsy/student/Lab4.scala. Here the AST is simplified down step by step until it returns a value. The interpreter uses functional programming concepts in that it expects to evaluate every expression down to a value. In the given example before, the AST would eventually be evaluated down to the value N(4.0) to represent the value the function would return of the number 4. 
