package com.github.jond3k.jonstructs.blocks

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait TimeoutBlock {

  /*/**
   *
   * @param ms
   * @param fn
   * @return
   */
  def timeout(ms: Long)(fn: => Unit) {
    timeout(ms, TimeUnit.MILLISECONDS)
  }

  /**
   *
   * @param t
   * @param unit
   * @param fn
   * @return
   */
  def timeout(t: Long, unit: TimeUnit)(fn: => Unit) {

  }*/
}
