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
package effectful

/**
  * A type-class for lifting the exec monad of a service into another
  * exec monad
  *
  * @tparam S the type of the service whose methods are all wrapped in
  *           an exec monad
  */
trait LiftService[S[_[_]]] {
  /**
    * Create a new instance of a service that returns computations
    * in a different exec monad by utilizing the supplied service and
    * its current exec monad
    *
    * @param s service to lift
    * @param C a type-class for capturing the computation of G inside F
    * @tparam F type of service's exec monad
    * @tparam G type of target exec monad
    * @return an instance of S[F] that utilizes the underlying S[E] to compute
    *         values by lifting all computed E[_] values into F[_]
    */
  def apply[F[_],G[_]](
    s: S[F]
  )(implicit
    C:CaptureTransform[F,G]
  ) : S[G]
}
