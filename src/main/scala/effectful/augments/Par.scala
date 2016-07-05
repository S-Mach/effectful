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

import scala.collection.generic.CanBuildFrom

// todo: docs
trait Par[E[_]] {

  def par[A,B](ea: =>E[A],eb: =>E[B]) : E[(A,B)]
  def par[A,B,C](ea: =>E[A],eb: =>E[B],ec: =>E[C]) : E[(A,B,C)]
  def par[A,B,C,D](ea: =>E[A],eb: =>E[B],ec: =>E[C],ed: =>E[D]) : E[(A,B,C,D)]
  // todo: gen more

  // todo: b/c of free monad interpreter this will prob have to be concrete Seq
  def parMap[M[AA] <: Seq[AA],A,B](
    items: M[A]
  )(
    f: A => E[B]
  )(implicit
    cbf: CanBuildFrom[Nothing,B,M[B]]
  ) : E[M[B]]

  def parFlatMap[M[AA] <: Seq[AA],A,B](
    items: M[A]
  )(
    f: A => E[Traversable[B]]
  )(implicit cbf: CanBuildFrom[Nothing,B,M[B]]) : E[M[B]]

  def parMapUnordered[M[AA] <: Traversable[AA],A,B](
    items: M[A]
  )(
    f: A => E[B]
  )(implicit
    cbf: CanBuildFrom[Nothing,B,M[B]]
  ) : E[M[B]]

  def parFlatMapUnordered[M[AA] <: Traversable[AA],A,B](
    items: M[A]
  )(
    f: A => E[Traversable[B]]
  )(implicit cbf: CanBuildFrom[Nothing,B,M[B]]) : E[M[B]]
}