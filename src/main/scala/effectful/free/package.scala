/*
                    ,i::,
               :;;;;;;;
              ;:,,::;.
            1ft1;::;1tL
              t1;::;1,
               :;::;               _____       __  ___              __
          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
       Lft1,:;:       , 1tfL:
       ;it1i ,,,:::;;;::1tti      effectful
         .t1i .,::;;; ;1tt        Copyright (c) 2016 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package effectful

import scala.language.implicitConversions
import effectful.augments._
import cats._
import cats.arrow.NaturalTransformation
import cats.data.Xor
import cats.data.Xor._

import scala.collection.generic.CanBuildFrom
import scala.concurrent.duration.FiniteDuration

package object free {
  implicit def monad_Free[Cmd[_]] = new Monad[({ type E[AA] = Free[Cmd,AA] })#E] {
    override def map[A, B](m: Free[Cmd, A])(f: (A) => B) =
      m.map(f)
    def flatMap[A, B](m: Free[Cmd, A])(f: (A) => Free[Cmd, B]) =
      m.flatMap(f)
    def pure[A](a: A) =
      Free.Pure(a)
    def widen[A, AA >: A](ea: Free[Cmd, A]) =
      ea.widen[AA]
  }

  implicit def exceptions_Free[Cmd[_]] = new Exceptions[({ type E[AA] = Free[Cmd,AA] })#E] {
    def attempt[A](_try: => Free[Cmd, A])(_catch: PartialFunction[Throwable, Free[Cmd, A]]) =
      Free.Attempt(_try,_catch)
    def attemptFinally[A, U](_try: => Free[Cmd, A])(_catch: PartialFunction[Throwable, Free[Cmd, A]])(_finally: => Free[Cmd, U]) =
      Free.AttemptFinally(_try,_catch,_finally)
    def failure(t: Throwable): Free[Cmd, Nothing] =
      Free.Failure(t)
    def success[A](a: A): Free[Cmd, A] =
      Free.Pure(a)
  }

  implicit def delays_Free[Cmd[_]] = new Delay[({ type E[AA] = Free[Cmd,AA] })#E] {
    def delay(duration: FiniteDuration): Free[Cmd, Unit] =
      Free.Delay(duration)
  }

  implicit def par_Free[Cmd[_]] = new Par[({ type E[AA] = Free[Cmd,AA] })#E] {
    def par[A, B](ea: => Free[Cmd, A], eb: => Free[Cmd, B]): Free[Cmd, (A, B)] =
      Free.Par2(ea,eb)
    def par[A, B, C](ea: => Free[Cmd, A], eb: => Free[Cmd, B], ec: => Free[Cmd, C]): Free[Cmd, (A, B, C)] =
      Free.Par3(ea,eb,ec)
    def par[A, B, C, D](ea: => Free[Cmd, A], eb: => Free[Cmd, B], ec: => Free[Cmd, C], ed: => Free[Cmd, D]): Free[Cmd, (A, B, C, D)] =
      Free.Par4(ea,eb,ec,ed)

    def parMap[M[AA] <: Seq[AA], A, B](items: M[A])(f: (A) => Free[Cmd, B])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): Free[Cmd, M[B]] =
      Free.ParMap(items,f)

    def parFlatMap[M[AA] <: Seq[AA], A, B](items: M[A])(f: (A) => Free[Cmd, Traversable[B]])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): Free[Cmd, M[B]] =
      Free.ParFlatMap(items,f)

    def parMapUnordered[M[AA] <: Traversable[AA], A, B](items: M[A])(f: (A) => Free[Cmd, B])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): Free[Cmd, M[B]] =
      Free.ParMapUnordered(items,f)

    def parFlatMapUnordered[M[AA] <: Traversable[AA], A, B](items: M[A])(f: (A) => Free[Cmd, Traversable[B]])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): Free[Cmd, M[B]] =
      Free.ParFlatMapUnordered(items,f)
  }

//  implicit def naturalTransformation_Free[Cmd1[_],Cmd2[_]](implicit
//    X: NaturalTransformation[Cmd1,Cmd2]
//  ) : NaturalTransformation[({ type F[A]=Free[Cmd1,A]})#F,({ type F[A]=Free[Cmd2,A]})#F] =
//    new NaturalTransformation[({ type F[A]=Free[Cmd1,A]})#F,({ type F[A]=Free[Cmd2,A]})#F] {
//      override def apply[A](f: Free[Cmd1, A]): Free[Cmd2, A] =
//        f.mapCmd[Cmd2]
//    }

  implicit def capture_Free[Cmd1[_]] =
    new Capture[({ type F[A] = Free[Cmd1,A]})#F] {
      def capture[A](a: => A) = Free(a)
    }

  implicit def captureTransform_Free[Cmd1[_],Cmd2[_]](implicit
    X:NaturalTransformation[Cmd1,Cmd2]
  ) =
    new CaptureTransform[({ type F1[A] = Free[Cmd1,A]})#F1,({ type F2[A] = Free[Cmd2,A]})#F2] {
      def apply[A](f: => Free[Cmd1, A]) =
        f.mapCmd[Cmd2]
    }

  // todo: how to generalize this?
  implicit def lift_disjunction_left[Cmd1[_],Cmd2[_]] : NaturalTransformation[Cmd1,({ type C[A] = Cmd1[A] Xor Cmd2[A] })#C] =
    new NaturalTransformation[Cmd1,({ type C[A] = Cmd1[A] Xor Cmd2[A] })#C] {
      override def apply[A](c: Cmd1[A]) = Left(c)
    }

  implicit def lift_disjunction_right[Cmd1[_],Cmd2[_]] : NaturalTransformation[Cmd2,({ type C[A] = Cmd1[A] Xor Cmd2[A] })#C] =
    new NaturalTransformation[Cmd2,({ type C[A] = Cmd1[A] Xor Cmd2[A] })#C] {
      override def apply[A](c: Cmd2[A]) = Right(c)
    }

}
