/*
 * CSCI 3155: Lab 4 TypeScript Worksheet
 *
 * This worksheet is a place to experiment with TypeScript expressions.
 */

// The new language features in Lab 4 are types, multi-parameter functions,
// and objects. We also extend JavaScripty with call-by-name parameters that
// is not in TypeScript or JavaScript.

const id_number = ((x: number) => x);

const plus = ((x: number) => (y: number) => x + y);
const plus_uncurried = ((x: number, y: number) => x + y);
const plus_pair = ((pair: {x: number; y: number}) => pair.x + pair.y);

const fst = ((x: number, y: number) => x);
const fst_alt = function (x: number, y: number): number { return x; };
const fst_pair = ((pair: {x: number; y: number}) => pair.x);

const inc = plus(1);

const do_numfun = function (f: (y: number) => number, x: number) { return f(x); };

console.log( id_number(3) );

console.log( plus(3)(4) );
console.log( plus_uncurried(3,4) );
console.log( plus_pair({x: 3, y: 4}) )
console.log( plus_pair({x: 1 + 2, y: 3 + 4}) )

console.log( fst(5, 6) );
console.log( fst_alt(5, 6) );
console.log( fst_pair({x: 5, y: 6}) );

console.log( do_numfun(inc, 2) );