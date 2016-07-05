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

// todo: docs
trait Capture[E[_]] {
  /**
    * Create an instance of E that may capture some of the
    * effects of a computation or might change how the computation
    * is executed (e.g. asynchronous or lazy)
    *
    * Note1: exceptions may or may not be captured
    * Note2: parameter is lazy to allow for widest possible capture
    * of effects
    *
    * @param a computation
    * @tparam A type of result of computation
    * @return an instance of E for the computation
    */
  def capture[A](a: => A) : E[A]
}