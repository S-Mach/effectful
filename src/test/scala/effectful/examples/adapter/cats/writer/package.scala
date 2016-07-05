package effectful.examples.adapter.cats

import cats.{Applicative, Functor, Monoid}
import cats.data.WriterT
import effectful.Capture
import effectful.augments._

package object writer {

  type LogWriterT[F[_],A] = WriterT[F,List[LogEntry],A]
  object LogWriterT {
    def construct[F[_],A](run: F[(List[LogEntry],A)]) : LogWriterT[F,A] =
      WriterT(run)
    def apply[F[_],A](a: A)(implicit
      F:Applicative[F]
    ) : LogWriterT[F,A] =
      WriterT.put(a)(Nil)
    def apply[F[_],A](
      entries: List[LogEntry],
      a: A
    )(implicit
      F:Applicative[F]
    ) : LogWriterT[F,A] =
      WriterT.put[F,List[LogEntry],A](a)(entries)
  }

  implicit def par_WriterT[F[_],L](implicit
    P:Par[F],
    F:Functor[F],
    L:Monoid[L]
  ) = new WriterTPar[F,L]

  implicit def delay_WriterT[F[_],L](implicit
    D:Delay[F],
    F:Functor[F],
    L:Monoid[L]
  ) = new WriterTDelay[F,L]

  implicit def exceptions_WriterT[F[_],L](implicit
    X:Exceptions[F],
    F:Functor[F],
    L:Monoid[L]
  ) = new WriterTExceptions[F,L]

  implicit def capture_LogWriterT[F[_],L](implicit
    F:Capture[F],
    L:Monoid[L]
  ) = new WriterTCapture[F,L]()
}
