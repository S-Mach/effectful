package effectful.examples.adapter.cats.writer

import cats.{Functor, Monoid}
import cats.data.WriterT
import effectful.augments.Exceptions

class WriterTExceptions[F[_],L](implicit
  X:Exceptions[F],
  F:Functor[F],
  L:Monoid[L]
) extends Exceptions[({type W[AA] = WriterT[F,L,AA]})#W] {
  import cats.syntax.functor._
  def attempt[A](_try: => WriterT[F, L, A])(_catch: PartialFunction[Throwable, WriterT[F, L, A]]) =
    WriterT {
      X.attempt(_try.run)(_catch.andThen(_.run))
    }
  def attemptFinally[A, U](_try: => WriterT[F, L, A])(_catch: PartialFunction[Throwable, WriterT[F, L, A]])(_finally: => WriterT[F, L, U]) = ???
  def failure(t: Throwable) =
    WriterT[F,L,Nothing](X.failure(t).map(identity))
  def success[A](a: A) =
    WriterT(X.success((L.empty,a)))
}
