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

import scala.collection.generic.CanBuildFrom
import cats._
import effectful.augments._

package object effectful { //extends CaptureTransform.Ops with Capture.Ops {
  // todo: figure out how this sugar is declared in emm
//  type |:[F[_],G[_]] = F[G[_]]
//  val |: = Nested

  implicit object capture_Id extends Capture[Id] {
    def capture[A](a: => A) = a
  }

  implicit def captureTransform_Id[G[_]](implicit
    G:Capture[G]
  ) = new CaptureTransform[Id,G] {
    def apply[A](f: => Id[A]) =
      G.capture(f)
  }

  implicit object par_Id extends Par[Id] {
    override def par[A, B](ea: => Id[A], eb: => Id[B]): (A, B) =
      (ea,eb)

    override def par[A, B, C](ea: => Id[A], eb: => Id[B], ec: => Id[C]): (A, B, C) =
      (ea,eb,ec)

    override def par[A, B, C, D](ea: => Id[A], eb: => Id[B], ec: => Id[C], ed: => Id[D]): (A, B, C, D) =
      (ea,eb,ec,ed)

    override def parMap[M[AA] <: Seq[AA], A, B](items: M[A])(f: (A) => Id[B])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): Id[M[B]] =
      items.map(f)(scala.collection.breakOut)

    override def parFlatMap[M[AA] <: Seq[AA], A, B](items: M[A])(f: (A) => Id[Traversable[B]])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): Id[M[B]] =
      items.flatMap(f)(scala.collection.breakOut)

    override def parMapUnordered[M[AA] <: Traversable[AA], A, B](items: M[A])(f: (A) => Id[B])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): Id[M[B]] =
      items.map(f)(scala.collection.breakOut)

    override def parFlatMapUnordered[M[AA] <: Traversable[AA], A, B](items: M[A])(f: (A) => Id[Traversable[B]])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]): Id[M[B]] =
      items.flatMap(f)(scala.collection.breakOut)
  }

  implicit object exceptions_Id extends impl.NoCaptureExceptions[Id] {
    implicit val E = implicitly[Monad[Id]]
  }

  implicit object delay_Id extends impl.BlockingDelay[Id] {
    override implicit val E = capture_Id
  }

  /**
    * Add the liftS method to any effectful service that uses the LiftS
    * type-class to lift the service's effect system monad into another
    * effect system's monad
    */
  implicit class ServicePML[S[_[_]],F[_]](val self: S[F]) extends AnyVal {
    def liftService[G[_]](implicit
      X:CaptureTransform[F,G],
      liftService:LiftService[S]
    ) : S[G] = liftService(self)
  }
}
