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

import effectful.augments.Par
import cats.Monad

import scala.collection.generic.CanBuildFrom

trait StdPar[E[_]] extends Par[E] {
  import Monad.ops._

  implicit val E:Monad[E]

  // Note: invoke lazy outside for-comp in case E is async + eager

  def par[A, B](ea: => E[A], eb: => E[B]) = {
    val _ea = ea
    val _eb = eb
    for {
      a <- _ea
      b <- _eb
    } yield (a,b)
  }

  def par[A, B, C](ea: => E[A], eb: => E[B], ec: => E[C]) = {
    val _ea = ea
    val _eb = eb
    val _ec = ec
    for {
      a <- _ea
      b <- _eb
      c <- _ec
    } yield (a,b,c)
  }

  def par[A, B, C, D](ea: => E[A], eb: => E[B], ec: => E[C], ed: => E[D]) = {
    val _ea = ea
    val _eb = eb
    val _ec = ec
    val _ed = ed
    for {
      a <- _ea
      b <- _eb
      c <- _ec
      d <- _ed
    } yield (a,b,c,d)
  }

  def parMap[M[AA] <: Seq[AA], A, B](items: M[A])(f: (A) => E[B])(implicit cbf: CanBuildFrom[Nothing,B,M[B]]) =
  // todo:
    ???
    //items.map(f).sequence(implicitly,cbf)

  def parFlatMap[M[AA] <: Seq[AA], A, B](items: M[A])(f: (A) => E[Traversable[B]])(implicit cbf: CanBuildFrom[Nothing,B,M[B]]) =
  // todo:
    ???
//    items.map(f)(scala.collection.breakOut).sequence.map(_.flatten)
  override def parMapUnordered[M[AA] <: Traversable[AA], A, B](items: M[A])(f: (A) => E[B])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): E[M[B]] = ???

  override def parFlatMapUnordered[M[AA] <: Traversable[AA], A, B](items: M[A])(f: (A) => E[Traversable[B]])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): E[M[B]] = ???
}

object StdPar {
  def apply[E[_]](implicit
    E:Monad[E]
  ) : StdPar[E] = {
    val _E = E
    new StdPar[E] {
      implicit val E = _E
    }
  }
}