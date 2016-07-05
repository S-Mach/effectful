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


import cats.Monad

import scala.collection.generic.CanBuildFrom
import scala.concurrent.duration.FiniteDuration

package object augments {
  implicit class ParHelper[M[AA] <: Seq[AA],A,E[_]](
    val self: M[A]
  ) extends AnyVal {
    def map[B](
      f: A => E[B]
    )(implicit
      p: Par[E],
      cbf: CanBuildFrom[Nothing,B,M[B]]
    ) : E[M[B]] =
      p.parMap(self)(f)
    def flatMap[B](
      f: A => E[Traversable[B]]
    )(implicit
      p: Par[E],
      cbf: CanBuildFrom[Nothing,B,M[B]]
    ) : E[M[B]] =
      p.parFlatMap(self)(f)
  }

  implicit class SeqPML[M[AA] <: Seq[AA],A](val self: M[A]) extends AnyVal {
    def par[E[_]] = new ParHelper[M,A,E](self)
  }

  implicit class E_Augments_PML[E[_]](val self:Monad[E]) extends AnyVal {
    def capture[A](f: => A)(implicit C:Capture[E]) : E[A] =
      C.capture(f)

    def attempt[A](
       _try: => E[A]
    )(
      _catch: PartialFunction[Throwable, E[A]]
    )(implicit
      X:Exceptions[E]
    ) : E[A] = X.attempt(_try)(_catch)

    def attemptFinally[A,U](
       _try: => E[A]
    )(
      _catch: PartialFunction[Throwable, E[A]]
    )(
      _finally: => E[U]
    )(implicit
      X:Exceptions[E]
    ) : E[A] =
      X.attemptFinally(_try)(_catch)(_finally)

    def success[A](a: A)(implicit X:Exceptions[E]) : E[A] =
      X.success(a)

    def failure[U](t: Throwable)(implicit X:Exceptions[E]) : E[U] =
      self.map(X.failure(t))(identity)


    def delay(d: FiniteDuration)(implicit D:Delay[E]) : E[Unit] =
      D.delay(d)

    def par[A,B](ea: =>E[A],eb: =>E[B])(implicit P:Par[E]) : E[(A,B)] =
      P.par(ea,eb)
    def par[A,B,C](ea: =>E[A],eb: =>E[B],ec: =>E[C])(implicit P:Par[E]) : E[(A,B,C)] =
      P.par(ea,eb,ec)
    def par[A,B,C,D](ea: =>E[A],eb: =>E[B],ec: =>E[C],ed: =>E[D])(implicit P:Par[E]) : E[(A,B,C,D)] =
      P.par(ea,eb,ec,ed)
  }


}
