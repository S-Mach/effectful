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
package effectful.augments

// todo: replace with cats MonadError
trait Exceptions[E[_]] {
  /**
    * Replacement for standard try/catch blocks when using an effect
    * capture monad. Using this method ensures proper handling of
    * exceptions for monads that capture exception and for those
    * that don't.
    *
    * Note: the try/catch block does not properly catch exceptions from
    * monads that capture exceptions inside their monad E[A], such as Try,
    * Future or scalaz.Task. Using a try/catch block around a monad
    * such as Future will never execute the catch block.
    *
    * @param _try code block to catch exceptions from
    * @param _catch exception handler
    * @tparam A type of expression
    * @return an instance of E
    */
  def attempt[A](
     _try: => E[A]
  )(
    _catch: PartialFunction[Throwable, E[A]]
  ) : E[A]

  /**
    * Replacement for standard try/catch/finally blocks when using an effect
    * capture monad. Using this method ensures proper handling of
    * exceptions for monads that capture exception and for those
    * that don't.
    *
    * Note: the try/catch block does not properly catch exceptions from
    * monads that capture exceptions inside their monad E[A], such as Try,
    * Future or scalaz.Task. Using a try/catch block around a monad
    * such as Future will never execute the catch block.
    *
    * @param _try code block to catch exceptions from
    * @param _catch exception handler
    * @tparam A type of expression
    * @return an instance of E
    */
  def attemptFinally[A,U](
     _try: => E[A]
  )(
    _catch: PartialFunction[Throwable, E[A]]
  )(
    _finally: => E[U]
  ) : E[A]

  /**
    * @return an instance of E that contains an already computed value
    */
  def success[A](a: A) : E[A]

  /**
    * @return an instance of E that contains an exception instead of a value
    */
  def failure(t: Throwable) : E[Nothing]
}