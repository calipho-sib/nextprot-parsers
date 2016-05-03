package org.nextprot.parser.bed.utils

class Memoize1[-T, +R](f: T => R) extends (T => R) {
   import scala.collection.mutable
   // map that stores (argument, result) pairs
   private[this] val vals = mutable.Map.empty[T, R]

   // Given an argument x, 
   //   If vals contains x return vals(x).
   //   Otherwise, update vals so that vals(x) == f(x) and return f(x).
   def apply(x: T): R = vals getOrElseUpdate (x, f(x))
}

object Memoize {
   /**
    * Memoize a unary (single-argument) function.
    *
    * @param f the unary function to memoize
    */
   def memoize[T, R](f: T => R): (T => R) = new Memoize1(f)

   /**
    * Memoize a binary (two-argument) function.
    * 
    * @param f the binary function to memoize
    * 
    * This works by turning a function that takes two arguments of type
    * T1 and T2 into a function that takes a single argument of type 
    * (T1, T2), memoizing that "tupled" function, then "untupling" the
    * memoized function.
    */
   def memoize[T1, T2, R](f: (T1, T2) => R): ((T1, T2) => R) = 
      Function.untupled(memoize(f.tupled))

   /**
    * Memoize a ternary (three-argument) function.
    *
    * @param f the ternary function to memoize
    */
   def memoize[T1, T2, T3, R](f: (T1, T2, T3) => R): ((T1, T2, T3) => R) =
      Function.untupled(memoize(f.tupled))

   // ... more memoize methods for higher-arity functions ...

   /**
    * Fixed-point combinator (for memoizing recursive functions).
    */
   def Y[T, R](f: (T => R) => T => R): (T => R) = {
      lazy val yf: (T => R) = memoize(f(yf)(_))
      yf
   }
}