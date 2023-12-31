package jsy.student

import jsy.lab4.Lab4Like

object Lab4 extends jsy.util.JsyApplication with Lab4Like {
  import jsy.lab4.ast._
  import jsy.lab4.Parser
  
  /*
   * CSCI 3155: Lab 4
   * Logan Schottland
   * 
   * Partner: Conor Harris
   * Collaborators: <Any Collaborators>
   */

  /*
   * Fill in the appropriate portions above by replacing things delimited
   * by '<'... '>'.
   * 
   * Replace the '???' expression with your code in each function.
   *
   * Do not make other modifications to this template, such as
   * - adding "extends App" or "extends Application" to your Lab object,
   * - adding a "main" method, and
   * - leaving any failing asserts.
   *
   * Your lab will not be graded if it does not compile.
   *
   * This template compiles without error. Before you submit comment out any
   * code that does not compile or causes a failing assert. Simply put in a
   * '???' as needed to get something that compiles without error. The '???'
   * is a Scala expression that throws the exception scala.NotImplementedError.
   */
  
  /*** Collections and Higher-Order Functions ***/
  
  /* Lists */
  
  // Remove consecutive duplicates in a list using recursion
  def compressRec[A](l: List[A]): List[A] = l match {
    case Nil | _ :: Nil => l
    case h1 :: (t1 @ (h2 :: _)) => {
      if(h1 == h2) compressRec(t1)
      else h1 :: compressRec(t1)
    }
  }

 // Remove consecutive duplicates in a list using foldRight 
  def compressFold[A](l: List[A]): List[A] = l.foldRight(Nil: List[A]){
    (h, acc) => {
      acc match {
        case Nil => h::acc
        case h1::t1 => {
          if(h1 == h) acc
          else h::acc
        }
      }
    } 
  }
  
  // Map the first element that satisfies the given function
  def mapFirst[A](l: List[A])(f: A => Option[A]): List[A] = l match {
    case Nil => l
    case h :: t => {
      f(h) match {
        case None => h :: mapFirst(t)(f)
        case Some(a) => a :: t
      }
    }
  }
  
  /* Trees */

  // Fold a tree from the left
  def foldLeft[A](t: Tree)(z: A)(f: (A, Int) => A): A = {
    def loop(acc: A, t: Tree): A = t match {
      case Empty => acc
      case Node(l, d, r) => loop(f(loop(acc,l),d), r)
    }
    loop(z, t)
  }

  // An example use of foldLeft
  def sum(t: Tree): Int = foldLeft(t)(0){ (acc, d) => acc + d }

  // Create a tree from a list. An example use of the
  // List.foldLeft method.
  def treeFromList(l: List[Int]): Tree =
    l.foldLeft(Empty: Tree){ (acc, i) => acc insert i }

  // Check if the tree is strictly ordered
  def strictlyOrdered(t: Tree): Boolean = {
    val (b, _) = foldLeft(t)((true, None: Option[Int])){
      case((false,a),b) => (false,a)
      case ((true,None),data)=> (true,Some(data))
      case((true,Some(prevData)),currData)=> if(currData > prevData) (true,Some(currData)) else (false,Some(currData))
    }
    b
  }

  /*** Rename bound variables in expression e ***/

 def rename(e: Expr)(fresh: String => String): Expr = {
  def ren(env: Map[String, String], e: Expr): Expr = {
    e match {
      case N(_) | B(_) | Undefined | S(_) => e
      case Print(e1) => Print(ren(env, e1))

      case Unary(uop, e1) => Unary(uop, ren(env, e1))
      case Binary(bop, e1, e2) => Binary(bop, ren(env, e1), ren(env, e2))
      case If(e1, e2, e3) => If(ren(env, e1), ren(env, e2), ren(env, e3))

      case Var(y) => Var(env.getOrElse(y, y))

      case Decl(mode, y, e1, e2) =>
        val yp = fresh(y)
        Decl(mode, yp, ren(env, e1), ren(env + (y -> yp), e2))

      case Function(p, params, tann, e1) => {
        val (pp, envp) = p match {
          case None => (None, env)
          case Some(x) => {
            val xp = fresh(x)
            (Some(xp), env + (x -> xp))
          }
        }
        val (paramsp, envpp) = params.foldRight((Nil: List[(String, MTyp)], envp)) {
          case ((yi, mt), (l, envAcc)) =>
            val yp = fresh(yi)
            ((yp, mt) :: l, envAcc + (yi -> yp))
        }
        Function(pp, paramsp, tann, ren(envpp, e1))
      }

      case Call(e1, args) => Call(ren(env, e1), args.map(ren(env, _)))

      case Obj(fields) => Obj(fields.map { case (f, e) => (f, ren(env, e)) })
      case GetField(e1, f) => GetField(ren(env, e1), f)
    }
  }
  ren(Map.empty, e)
}


  /*** Type Inference ***/

  // While this helper function is completely given, this function is
  // worth studying to see how library methods are used.
  def hasFunctionTyp(t: Typ): Boolean = t match {
    case TFunction(_, _) => true
    case TObj(fields) if (fields exists { case (_, t) => hasFunctionTyp(t) }) => true
    case _ => false
  }
  
  def typeof(env: TEnv, e: Expr): Typ = {
    def err[T](tgot: Typ, e1: Expr): T = throw StaticTypeError(tgot, e1, e)

    e match {
      case Print(e1) => typeof(env, e1); TUndefined
      case N(_) => TNumber
      case B(_) => TBool
      case Undefined => TUndefined
      case S(_) => TString
      case Var(x) => env.getOrElse(x,err(TUndefined,e))
      case Unary(Neg, e1) => typeof(env, e1) match {
        case TNumber => TNumber
        case tgot => err(tgot, e1)
      }
      case Unary(Not, e1) => typeof(env, e1) match {
        case TBool => TBool
        case tgot => err(tgot, e1)
      }
      case Binary(Plus, e1, e2) =>{
        (typeof(env, e1),typeof(env, e2)) match{
          case (TString, TString)=> TString
          case (TNumber, TNumber)=> TNumber
          case (a1,a2)=> TNumber
        }
      }
      case Binary(Minus|Times|Div, e1, e2) => 
        TNumber
      case Binary(Eq|Ne, e1, e2) =>{
        (typeof(env,e1), typeof(env,e2)) match{
          case (tgot,_) => err(tgot, e1)
          //case (_,tgot) => err(tgot, e2)
          case _ => TBool
        }
      }
      case Binary(Lt|Le|Gt|Ge, e1, e2) =>
        TBool
      case Binary(And|Or, e1, e2) =>
        TBool
      case Binary(Seq, e1, e2) =>
        typeof(env,e2)
      case If(e1, e2, e3) =>{
        if(toBoolean(e1)) typeof(env,e2) else typeof(env,e3)
      }
      case Obj(fields) => TObj(fields map {case (a,b) => (a,typeof(env,b))}) 
      case GetField(Obj(fields), f) => typeof(env,fields.getOrElse(f,throw StuckError(Obj(fields))))

      case Decl(m,x,e1,e2)=>{
        val t1 = typeof(env, e1)
        val newenv = extend(env, x, t1)
        val t2 = typeof(newenv, e2)
        t2
      }

      case Function(p, params, tann, e1) => {
        // Bind to env1 an environment that extends env with an appropriate binding if
        // the function is potentially recursive.
        val env1 = (p, tann) match { 
          case (None,_)=> env
          case(Some(x), Some(y)) => extend(env, x, TFunction(params,y))
          /***** Add cases here *****/
          case _ => err(TUndefined, e1)
        }
        // Bind to env2 an environment that extends env1 with bindings for params.
        /*val env2 = params.foldLeft(env1){
          case(env1, (si,MTyp(mi,ti))) => extend(env1, si, ti)
        }*/
        val env2 = params.foldLeft(env1){(envacc,parami)=>
          val (xi, MTyp(_,ti)) = parami
          extend(envacc, xi, ti)
        }
        // Infer the type of the function body
        val t1 = typeof(env2, e1)
        // Check with the possibly annotated return type
        tann match {
          case None => TFunction(params,t1)
          case Some(e) => {
            if(e == t1) TFunction(params, t1)
            else err(t1, e1)
          }
        }
      }
      case Call(e1, args) => typeof(env, e1) match {
        case TFunction(params, tret) if (params.length == args.length) =>
          (params zip args).foreach {case (parami, ei)=>
            val (_,MTyp(_,ti)) = parami
            val tgot = typeof(env,ei)
            if(tgot == ti) () else err(tgot,ei)
          }
          tret
        case tgot => err(tgot, e1)
      }

    }
  }
  
  /*** Small-Step Interpreter ***/



  /* Helper functions to change type */
  def toNumber(v: Expr): Double = {
    require(isValue(v))
    (v: @unchecked) match {
      case N(n) => n
      case B(true)=>1
      case B(false)=>0
      case Undefined=>Double.NaN
      case S("undefined")=>Double.NaN
      case S(s)=>try s.toDouble catch {case _: Throwable=>Double.NaN}
      case Function(_,_, _, _) => Double.NaN
    }
  }
  
  def toBoolean(v: Expr): Boolean = {
    require(isValue(v))
    (v: @unchecked) match {
      case B(b) => b
      case N(0) => false
      case N(n) if n.isNaN=>false
      case N(n) =>true
      case S("")=>false
      case S(s)=>true
      case Undefined=>false
      case Function(_,_, _, _) => true
    }
  }
  
  def toStr(v: Expr): String = {
    require(isValue(v))
    (v: @unchecked) match {
      case S(s) => s
      case B(true) => "true"
      case B(false) => "false"
      case N(n) => if(n.isWhole){n.toInt.toString()} else n.toString
      case Undefined => "undefined"
        // Here in toStr(Function(_, _, _)), we will deviate from Node.js that returns the concrete syntax
        // of the function (from the input program).
      case Function(_,_, _, _) => "function"
      //case _ => ??? // delete this line when done
    }
  }
  /*
   * Helper function that implements the semantics of inequality
   * operators Lt, Le, Gt, and Ge on values.
   *
   * We suggest a refactoring of code from Lab 2 to be able to
   * use this helper function in eval and step.
   *
   * This should the same code as from Lab 3.
   */
  def inequalityVal(bop: Bop, v1: Expr, v2: Expr): Boolean = {
    require(isValue(v1), s"inequalityVal: v1 ${v1} is not a value")
    require(isValue(v2), s"inequalityVal: v2 ${v2} is not a value")
    require(bop == Lt || bop == Le || bop == Gt || bop == Ge)
    (bop) match {
      case Lt=>{
        (v1,v2) match{
          case(S(a1),S(a2))=>if(a1 < a2) true else false
          case (a1,a2)=> if(toNumber(a1) < toNumber(a2)) true else false
        }
      }
      case Le=>{
        (v1, v2) match{
          case(S(a1),S(a2))=>if(a1 <= a2) true else false
          case (a1, S(a2))=> if(toStr(a1)=="undefined") false else {if(toStr(a1) <= a2) true else false}
          case (a1,a2)=> if(toNumber(a1) <= toNumber(a2)) true else false
        }
      }
      case Gt=>{
        (v1,v2) match{
          case(S(a1),S(a2))=>if(a1 > a2) true else false
          case (a1,a2)=> if(toNumber(a1) > toNumber(a2)) true else false
        }
      }
      case Ge=>{
        (v1,v2) match{
          case(S(a1),S(a2))=>if(a1 >= a2) true else false
          case (a1,a2)=> if(toNumber(a1) >= toNumber(a2)) true else false
        }
      }
    }
  }

  /* This should be the same code as from Lab 3 */
  def iterate(e0: Expr)(next: (Expr, Int) => Option[Expr]): Expr = {
    def loop(e: Expr, n: Int): Expr = {
      val oe=next(e,n)
      oe match{
        case None=> e
        case Some(expr)=> loop(expr, n+1)
      }
    }
    loop(e0, 0)
  }

  /* Capture-avoiding substitution in e replacing variables x with esub. */
  def substitute(e: Expr, esub: Expr, x: String): Expr = {
    def subst(e: Expr): Expr = e match {
      case N(_) | B(_) | Undefined | S(_) => e
      case Print(e1) => Print(subst(e1))
        /***** Cases from Lab 3 */
      case Unary(uop, e1) => Unary(uop, substitute(e1, esub, x))
      case Binary(bop, e1, e2) =>{Binary(bop, substitute(e1, esub, x), substitute(e2, esub, x))}
      case If(e1, e2, e3) => If(substitute(e1, esub, x), substitute(e2, esub, x), substitute(e3, esub, x))
      case Var(y) => if(y == x) esub else e
      case Decl(mode, y, e1, e2) => if(y==x) Decl(mode, y,substitute(e1,esub,y), e2) else Decl(mode, y,substitute(e1,esub,x),substitute(e2,esub,x))
        /***** Cases needing adapting from Lab 3 */
      case Function(p, params, tann, e1) =>
        Function(p,params,tann, substitute(e1,esub,x))
      case Call(e1, args) => Call(substitute(e1,esub,x), args map { arg => subst(arg)})
        /***** New cases for Lab 4 */
      case Obj(fields) => Obj(fields map {case (a,b) => (a,substitute(b,esub,x))})
      case GetField(e1, f) => GetField(substitute(e1,esub,x),f)
    }

    val fvs = freeVars(e)
    def fresh(x: String): String = if (fvs.contains(x)) fresh(x + "$") else x

    subst(e) // change this line if/when you implement capture-avoidance for call-by-name
  }

  /* Helper function for implementing call-by-name: check whether or not an expression is reduced enough to be applied given a mode. */
  def isRedex(mode: Mode, e: Expr): Boolean = mode match {
    case MConst => !isValue(e)
    case MName => false
  }

  /* A small-step transition. */
  def step(e: Expr): Expr = {
    require(!isValue(e), s"step: e ${e} to step is a value")
    e match {
      /* Base Cases: Do Rules */
      case Print(v1) if isValue(v1) => println(pretty(v1)); Undefined
        /***** Cases needing adapting from Lab 3. */
      case Unary(Neg, N(v1)) => N(-v1)
      case Decl(m, x,v1,e2) if isValue(v1)=> substitute(e2,v1,x)
        /***** More cases here */
      case Unary(Not,B(v1)) => B(!v1)
      case Binary(Seq,v1,e2) =>{
        val n1 = v1
        e2
      }
      case Binary(bop @ (Lt|Gt|Le|Ge),v1,v2) if isValue(v1) && isValue(v2)=>{
        (v1,v2) match{
          case (S(a1), S(a2))=> if(inequalityVal(bop, S(a1), S(a2))) B(true) else B(false)
          case (a1,a2)=> if(toNumber(a1).isNaN || toNumber(a2).isNaN) B(false) else B(inequalityVal(bop,a1, a2))
        }
      }
      case Binary(Times, N(v1), N(v2)) =>  N(v1*v2)
      case Binary(Div, N(v1), N(v2)) => N(v1/v2)

      case Binary(Plus, v1, v2) =>{
        (v1,v2) match{
          case(N(a1), N(a2))=> N(a1+a2)
          case (S(a1), S(a2)) => S(a1+a2)
        }
      }
      case Binary(Minus, N(v1), N(v2)) => N(v1-v2)
      // case Binary(Eq, v1, v2)if isValue(v1) && isValue(v2)=>{ B(v1 == v2)
      //   // (v1, v2) match{
          
      //   //   case(S(a1),S(a2))=> if(a1 == a2) B(true) else B(false)
      //   //   case (B(a1), B(a2))=> if(a1 == a2) B(true) else B(false)
      //   //   case (Function(_,_,_,_),_) => throw DynamicTypeError(e)
      //   //   case (_,Function(_,_,_,_)) => throw DynamicTypeError(e)
      //   //   case (a1,a2)=> if(toNumber(a1).isNaN || toNumber(a2).isNaN) B(false) else {if(a1 == a2) B(true) else B(false)}
      //   //   case _ => B(false)
      //   // }
      // }
      case Binary(Ne, v1, v2) if isValue(v1) && isValue(v2)=>{
        (v1, v2) match{
          case(S(a1),S(a2))=> if(a1 == a2) B(false) else B(true)
          case (B(a1), B(a2))=> if(a1 == a2) B(false) else B(true)
          //case (Function(_,_,_,_),_) => throw DynamicTypeError(e)
          //case (_,Function(_,_,_,_)) => throw DynamicTypeError(e)
          case (a1,a2)=> if(a1 == a2) B(false) else B(true)
          case _ => B(true) 
        }
      }
      case Binary(And, B(v1), B(v2)) =>{
        v1 match{
          case true => B(v2)
          case false => B(false)
        }
      }
      case Binary(Or, B(e1), B(e2)) =>{
        e1 match {
          case true => B(true)
          case false => B(e2)
        }
      }
      case If(B(v1),e2,e3) =>{
        if(v1) e2 else e3
      }
      case Call(v1, args) if isValue(v1) =>
        v1 match {
          case Function(p, params, _, e1) => {
            val pazip = params zip args
            if (pazip forall {case ((_, MTyp(mi,_)), ei) => isValue(ei) }) {
              val e1p = pazip.foldRight(e1) {
                //I think this line is wrong 
                case (((s_i, MTyp(m_i, t_i)), e_i),acc) => substitute(acc, e_i,s_i)
                //case (((xi,_), vi), acc) => substitute(acc, vi, xi)
              }
              p match {
                case None => e1p
                case Some(x1) => substitute(e1p, v1, x1)
              }
            }
            else {
              val pazipp = mapFirst(pazip) {
                case ((xi,MTyp(modei,typi)),ei) => {
                  if(isRedex(modei, ei)){ 
                    Some((xi,MTyp(modei,typi)),step(ei))
                  }
                  else{
                    None
                  }
                }
              }
              val list = pazipp map {
                case (_,ei) => ei
              }
              Call(v1,list)
            }
          }
          case _ => throw StuckError(e)
        }
        /***** New cases for Lab 4. */
      case GetField(Obj(fields), f) => fields.getOrElse(f,throw StuckError(e))
      /* Inductive Cases: Search Rules */
      case Print(e1) => Print(step(e1))
        /***** Cases from Lab 3. */
      case Unary(uop @ (Neg|Not), e1)=>{
      Unary(uop,step(e1))
      }
      case Binary(Seq, e1, e2) =>{
        if(isValue(e1)) e2 else Binary(Seq, step(e1), e2)
      }
      case Binary(bop @ (Lt|Le|Gt|Ge|Eq|Ne|Times|Div|Plus|Minus|And|Or), e1,e2)=>{
        if(isValue(e1)) Binary(bop,e1, step(e2)) else Binary(bop, step(e1), e2)
      }
      case If(e1,e2,e3)=>{
        If(step(e1), e2, e3)
      }
        /***** More cases here */
        /***** Cases needing adapting from Lab 3 */
      //case Call(v1 @ Function(_, _, _, _), args) => ???
      case Call(e1, args) => Call(step(e1), args)
      case Decl(m, x, e1, e2) => Decl(m, x, step(e1), e2)
        /***** New cases for Lab 4. */
      case Obj(fields) => Obj(fields map {case (a,b) if(!isValue(b)) => (a,step(b))})
      case GetField(e1, f) => GetField(step(e1),f)
      /* Everything else is a stuck error. Should not happen if e is well-typed.
       *
       * Tip: you might want to first develop by comment out the following line to see which
       * cases you have missing. You then uncomment this line when you are sure all the cases
       * that you have left are ones that should be stuck.
       */
      case _ => throw StuckError(e)
    }
  }
  
  
  /* External Interfaces */
  
  //this.debug = true // uncomment this if you want to print debugging information
  this.maxSteps = Some(1000) // comment this out or set to None to not bound the number of steps.
  this.keepGoing = true // comment this out if you want to stop at first exception when processing a file
}
