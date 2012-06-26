package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.TimeUnit

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait RetryBlock {

  def retry(ms: Long)(fn: => Unit) {
    retry(-1, ms)
  }

  /**
   *
   * @param times
   * @param ms
   * @param fn
   * @return
   */
  def retry(times: Int, ms: Long)(fn: => Unit) {
    retry(times, ms, TimeUnit.MILLISECONDS)
  }

  /**
   *
   * @param times
   * @param every
   * @param unit
   * @param fn
   * @return
   */
  def retry(times: Int, every: Long, unit: TimeUnit)(fn: => Unit) {

  }
}
