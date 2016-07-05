package effectful.examples.adapter.cats.writer

import java.time.Instant

import cats.Applicative
import effectful.examples.effects.logging.Logger

class WriterLoggerT[F[_]](name: String)(implicit F:Applicative[F]) extends Logger[({ type L[AA] = LogWriterT[F,AA]})#L] {
  type LogWriter[AA] = LogWriterT[F,AA]

  override def trace(message: =>String): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Trace,message,None,Instant.now()) :: Nil,())

  override def trace(message: =>String, cause: Throwable): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Trace,message,Some(cause),Instant.now()) :: Nil,())

  override def debug(message: =>String): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Debug,message,None,Instant.now()) :: Nil,())

  override def debug(message: =>String, cause: Throwable): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Debug,message,Some(cause),Instant.now()) :: Nil,())

  override def info(message: =>String): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Info,message,None,Instant.now()) :: Nil,())

  override def info(message: => String, cause: Throwable): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Info,message,None,Instant.now()) :: Nil,())

  override def warn(message: =>String): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Warn,message,None,Instant.now()) :: Nil,())

  override def warn(message: =>String, cause: Throwable): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Warn,message,Some(cause),Instant.now()) :: Nil,())

  override def error(message: =>String): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Error,message,None,Instant.now()) :: Nil,())

  override def error(message: =>String, cause: Throwable): LogWriter[Unit] =
    LogWriterT(LogEntry(name,LogLevel.Error,message,Some(cause),Instant.now()) :: Nil,())
}

object WriterLoggerT {
  def apply[F[_]](name: String)(implicit F:Applicative[F]) : WriterLoggerT[F] =
    new WriterLoggerT(name)
}