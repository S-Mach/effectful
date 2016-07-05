package effectful.examples.pure.user

import cats.data.Xor

trait UserLogins[E[_]] {
  import UserLogins._

  def login(username: String, password: String) : E[LoginFailure Xor Token]
}

object UserLogins {
  type Token = String
  sealed trait LoginFailure
  object LoginFailure {
    case object PasswordMismatch extends LoginFailure
    case object UserDoesNotExist extends LoginFailure
  }
}
