package effectful.examples.adapter.cats.writer

import cats.Monoid
import cats.data.WriterT
import effectful.Capture

class WriterTCapture[F[_],L](implicit
  F:Capture[F],
  L:Monoid[L]
) extends Capture[({type W[AA] = WriterT[F,L,AA]})#W] {
  def capture[A](a: => A) =
    WriterT(F.capture((L.empty,a)))
}
