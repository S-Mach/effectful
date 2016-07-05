package effectful.examples.adapter.cats.writer

import cats.{Functor, Monoid}
import cats.data.WriterT
import effectful.augments.Delay
import scala.concurrent.duration.FiniteDuration

class WriterTDelay[F[_],L](implicit
  D:Delay[F],
  F:Functor[F],
  L:Monoid[L]
) extends Delay[({type W[AA] = WriterT[F,L,AA]})#W] {
  import cats.syntax.functor._
  def delay(duration: FiniteDuration) =
    WriterT(D.delay(duration).map(_ => (L.empty,())))
}
