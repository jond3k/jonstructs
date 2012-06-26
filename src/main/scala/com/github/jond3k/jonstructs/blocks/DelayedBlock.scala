package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.TimeUnit

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait DelayedBlock extends BlockScheduling {

  /**
   *
   * @param ms
   * @param fn
   * @return
   */
  /*def delayed(ms: Long)(fn: => Unit) {
    delayed(ms, TimeUnit.MILLISECONDS)
  }

  /**
   *
   * @param t
   * @param unit
   * @param fn
   * @return
   */
  def delayed(t: Long, unit: TimeUnit)(fn: => Unit) {
    runBlockSchedule(fn, t, unit)
  }*/
}
