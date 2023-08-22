# JavaScript Interpreter
This program is a JavaScripty (Somewhat simplified although almost totally functional JavaScript variant) Interpreter written in Scala. It was written as a project for a programming languages class. As the architecture of the interpreter is somewhat complex I will explain here how it works and in what files different parts of the implementation can be found. Another good place to look to understand this program better is in the lab4.pdf file in the root of the project. This file is the assignment and tells what part of the interpreter was built in this section of the lab. Note that sections that aren't in the lab4.pdf were built in earlier parts of the course (i.e. labs 1-3)

# Parsing of Javascripty
The parsing of JavaScripty into AST (Abstract Syntax Trees), which our interpreter can understand is done in the file ./src/main/scala/jsy/lab4/Parser.scala. You can read through the implementation of this to understand it better but I will give an example of the inputs and outputs here. An example JavaScripty input might look like this. This declares a function that takes in x and y and returns x. Then this function is called with (4,true)

``` function (x: number, y: bool) { return x} (4, true) ```

After going through the parser this function will be turned into the following AST to be evaluated by the interpreter. 


```  Call(Function(None,List((x,MTyp(MConst,TNumber)),(y,MTyp(MConst,TBool))),None,Var(x)),List(N(4.0), B(true))) ```

