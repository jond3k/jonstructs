package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.TimeUnit

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait EveryBlock {

  /**
   *
   * @param ms
   * @param fn
   * @return
   */
  def every(ms: Long)(fn: => Unit) {
    every(ms, TimeUnit.MILLISECONDS)
  }

  /**
   *
   * @param t
   * @param unit
   * @param fn
   * @return
   */
  def every(t: Long, unit: TimeUnit)(fn: => Unit) {

  }

}
