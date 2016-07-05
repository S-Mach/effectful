package effectful

import cats._
import cats.arrow.NaturalTransformation
// combine capture, natural transformation & monad
trait CaptureTransform[F[_],G[_]] {
  def apply[A](f: => F[A]) : G[A]
}

//object CaptureTransform {
//  trait Ops {
//    implicit def mkCaptureTransform[F[_],G[_]](implicit
//      X:Capture[G],
//      F:Monad[G],
//      N:NaturalTransformation[F,G]
//    ) : CaptureTransform[F,G] = new CaptureTransform[F,G] {
//      def apply[A](f: => F[A]) =
//        {
//          import Monad.ops._
//          X.capture(N(f)).flatten
//        }
//    }
//  }
//  object ops extends Ops
//}