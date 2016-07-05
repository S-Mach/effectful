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
package effectful.impl

import effectful.augments.Exceptions
import cats.Monad

/**
  * An instance of Exceptions for monads that don't capture exceptions
  * in the monad
  */
trait NoCaptureExceptions[E[_]] extends Exceptions[E] {
  implicit val E:Monad[E]

  override def attempt[A](
   _try: =>E[A]
  )(
   _catch: PartialFunction[Throwable, E[A]]
  ): E[A] =
    try { _try } catch _catch

  override def attemptFinally[A,U](
    _try: => E[A]
  )(
    _catch: PartialFunction[Throwable, E[A]]
  )(
    _finally: => E[U]
  ): E[A] =
    try { _try } catch _catch finally _finally

  override def success[A](a: A): E[A] =
    E.pure(a)

  override def failure(t: Throwable): E[Nothing] =
    throw t
}
