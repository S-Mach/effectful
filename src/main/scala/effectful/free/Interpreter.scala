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
package effectful.free

import cats._
import cats.data.Xor
import cats.data.Xor._
import effectful.Capture
import effectful.augments._

// todo: docs
trait Interpreter[Cmd[_],E[_]] {
  val C:Capture[E]
  val M:Monad[E]
  val D:Delay[E]
  val P:Par[E]
  val X:Exceptions[E]

  def apply[A](cmd: Cmd[A]) : E[A]
}

object Interpreter {
  abstract class Abstract[Cmd[_],E[_]](implicit
    val C:Capture[E],
    val M:Monad[E],
    val D:Delay[E],
    val P:Par[E],
    val X:Exceptions[E]
  ) extends Interpreter[Cmd,E]

  class XorInterpreter[Cmd1[_],Cmd2[_],E[_]](
    left:Interpreter[Cmd1,E],
    right:Interpreter[Cmd2,E]
  )(implicit
    val C:Capture[E],
    val M:Monad[E],
    val D:Delay[E],
    val P:Par[E],
    val X:Exceptions[E]
  ) extends Interpreter[({ type C[AA]=Xor[Cmd1[AA],Cmd2[AA]] })#C,E] {
    type C[AA]=Xor[Cmd1[AA],Cmd2[AA]]
    def apply[A](cmd: C[A]) =
      cmd match {
        case Left(leftCmd) => left(leftCmd)
        case Right(rightCmd) => right(rightCmd)
      }

  }
}