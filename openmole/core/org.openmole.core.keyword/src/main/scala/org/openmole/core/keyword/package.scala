package org.openmole.core

/**
 * Generic keywords and their DSL
 */
package keyword {

  case class In[+A, +B](value: A, domain: B)
  case class Under[+A, +B](value: A, under: B)
  case class :=[+A, +B](value: A, equal: B)
  case class Negative[+A](value: A)
  case class Aggregate[+A, +B](value: A, aggregate: B)
  case class Delta[+A, +B](value: A, delta: B)
  case class As[+A, +B](value: A, as: B)
  case class By[+A, +B](value: A, by: B)

  trait KeyWordPackage {
    implicit class InDecorator[A](a: A) {
      def in[B](b: B) = In(a, b)
    }

    implicit class UnderDecorator[A](a: A) {
      def under[B](b: B) = Under(a, b)
    }

    implicit class EqualDecorator[A](a: A) {
      def :=[B](b: B) = new :=(a, b)
    }

    implicit class NegativeDecorator[A](a: A) {
      def unary_- = Negative(a)
    }

    implicit class AggregateDecorator[A](a: A) {
      def aggregate[B](b: B) = Aggregate(a, b)
      //def aggregate[B, C](b: B ⇒ C) = Aggregate(a, b)
    }

    implicit class DeltaDecorator[A](a: A) {
      def delta[B](b: B) = Delta(a, b)
    }

    implicit class AsDecorator[A](a: A) {
      def as[B](b: B) = As(a, b)
    }

    implicit class ByDecorator[A](a: A) {
      def by[B](b: B) = By(a, b)
    }
  }

}

package object keyword extends KeyWordPackage
