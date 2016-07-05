package effectful.examples.adapter.cats.writer

import cats.{Functor, Monoid}
import cats.data.WriterT
import effectful.augments.Par

import scala.collection.generic.CanBuildFrom

class WriterTPar[F[_],L](implicit
  P:Par[F],
  F:Functor[F],
  L:Monoid[L]
) extends Par[({type W[AA] = WriterT[F,L,AA]})#W] {
  import cats.syntax.functor._

  def par[A, B](ea: => WriterT[F, L, A], eb: => WriterT[F, L, B]) =
    WriterT {
      P.par(ea.run,eb.run).map { case ((l1,a),(l2,b)) =>
        (L.combine(l1,l2),(a,b))
      }
    }

  def par[A, B, C](ea: => WriterT[F, L, A], eb: => WriterT[F, L, B], ec: => WriterT[F, L, C]) = ???

  def par[A, B, C, D](ea: => WriterT[F, L, A], eb: => WriterT[F, L, B], ec: => WriterT[F, L, C], ed: => WriterT[F, L, D]) = ???

  // todo:
  def parMap[M[AA] <: Seq[AA], A, B](items: M[A])(f: (A) => WriterT[F, L, B])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]) = ???

  def parFlatMap[M[AA] <: Seq[AA], A, B](items: M[A])(f: (A) => WriterT[F, L, Traversable[B]])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]) = ???

  def parMapUnordered[M[AA] <: Traversable[AA], A, B](items: M[A])(f: (A) => WriterT[F, L, B])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]) = ???

  def parFlatMapUnordered[M[AA] <: Traversable[AA], A, B](items: M[A])(f: (A) => WriterT[F, L, Traversable[B]])(implicit cbf: CanBuildFrom[Nothing, B, M[B]]) = ???
}

