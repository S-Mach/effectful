package effectful.examples


import scala.concurrent.duration._
import cats.data.Xor
import cats._
import cats.std.list._
import effectful._
import effectful.augments.{Delay, Exceptions, Par}
import effectful.examples.adapter.akka.ExecFuture
import effectful.examples.adapter.cats.writer._
import effectful.examples.effects.logging.free._
import effectful.examples.adapter.slf4j.Slf4jLogger
import effectful.examples.effects.sql.free._
import effectful.examples.pure.uuid.impl.JavaUUIDs
import effectful.examples.mapping.sql._
import effectful.examples.pure.user.impl._
import effectful.examples.pure.user._
import effectful.examples.pure._
import effectful.examples.pure.dao.sql.impl.SqlDocDaoImpl
import effectful.examples.pure.uuid.UUIDs.UUID
import effectful.free._
import s_mach.concurrent.ScheduledExecutionContext

import scala.concurrent.Future

object FreeMonadExample {
//  import Capture.ops._
//  import CaptureTransform.ops._

  type Cmd[A] = SqlDriverCmd[A] Xor LoggerCmd[A]
  type E[A] = Free[Cmd,A]

  implicit val uuids = new JavaUUIDs

  val sqlDriver = new FreeSqlDriver

  val tokensDao = new SqlDocDaoImpl[String,Tokens.TokenInfo,E](
    sql = sqlDriver.liftService[E],
    recordMapping = tokenInfoRecordMapping,
    metadataMapping = tokenInfoMetadataRecordMapping
  )

  val tokens = new TokensImpl[E](
    uuids = uuids.liftService[E],
    tokensDao = tokensDao,
    tokenDefaultDuration = 10.days,
    logger = FreeLogger("tokens").liftService[E]
  )

  val passwords = new PasswordsImpl[E](
    passwordMismatchDelay = 5.seconds,
    logger = FreeLogger("passwords").liftService[E]
  )


  val userDao = new SqlDocDaoImpl[UUID,UsersImpl.UserData,E](
    sql = sqlDriver.liftService[E],
    recordMapping = userDataRecordMapping,
    metadataMapping = userDataMetadataRecordMapping
  )

  val users = new UsersImpl[E](
    usersDao = userDao,
    passwords = passwords,
    logger = FreeLogger("users").liftService[E]
  )

  val userLogins = new UserLoginsImpl[E](
    users = users,
    tokens = tokens,
    passwords = passwords,
    logger = FreeLogger("userLogins").liftService[E]
  )

  // todo: generalize interpreter for any disjunction of commands
  val futInterpreter : Interpreter[Cmd,FutureLogWriterExample.E] = {
    import FutureLogWriterExample._
    type EE[A] = FutureLogWriterExample.E[A]

    new Interpreter.XorInterpreter[SqlDriverCmd,LoggerCmd,EE](
      new SqlDriverCmdInterpreter[EE](
        sqlDriver = FutureLogWriterExample.sqlDriver.liftService[EE]
      ),
      new LoggerCmdInterpreter[EE](
        // todo: memoize these
        loggerName => WriterLogger(loggerName)
      )
    )
  }

  val idInterpreter : Interpreter[Cmd,Id] =
    new Interpreter.XorInterpreter[SqlDriverCmd,LoggerCmd,Id](
    new SqlDriverCmdInterpreter[Id](
      sqlDriver = IdExample.sqlDriver
    ),
    new LoggerCmdInterpreter[Id](
      // todo: memoize these
      loggerName => Slf4jLogger(loggerName)
    )
  )
}
