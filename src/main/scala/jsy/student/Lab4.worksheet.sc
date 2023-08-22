/*
 * CSCI 3155: Lab 4 Worksheet
 *
 * This worksheet demonstrates how you could experiment
 * interactively with your implementations in Lab4.scala.
 */

// Imports the parse function from jsy.lab4.Parser
import jsy.lab4.Parser.{parse,parseFile}

// Imports the ast nodes
import jsy.lab4.ast._

// Imports all of the functions form jsy.student.Lab4 (your implementations in Lab4.scala)
import jsy.student.Lab4._

// Try compressRec
//val cr1 = compressRec(List(1, 2, 2, 3, 3, 3))

// Parse functions with possibly multiple parameters and type annotations.
parse("function fst(x: number, y: number): number { return x }")
parse("function (x: number) { return x }")
parse("function (f: (y: number) => number, x: number) { return f(x) }")

// Parse objects
parse("{ f: 0, g: true }")
parse("x.f")

// Run your type checker
// - inferType calls your typeof
//inferType("1 + 2")

// Run your small-step interpreter
// - iterateStep calls your iterate and your step
//iterateStep("1 + 2");

// Parse the JavaScripty expression in your worksheet
val worksheetJsy = parseFile("src/main/scala/jsy/student/Lab4.worksheet.ts")

// Interpret the JavaScripty expression in your worksheet
//inferType(worksheetJsy)
//iterateStep(worksheetJsy)