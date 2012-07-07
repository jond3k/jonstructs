package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.TimeUnit
import java.util

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait EveryBlock extends SchedulingBlock {

  /**
   *
   * @param ms
   * @param fn
   * @return
   */
  def every(ms: Long)(fn: => Unit): util.TimerTask = {
    every(ms, TimeUnit.MILLISECONDS)(fn)
  }

  /**
   *
   * @param t
   * @param unit
   * @param fn
   * @return
   */
  def every(t: Long, unit: TimeUnit)(fn: => Unit): util.TimerTask = {
    val task = new ScheduleBlockTimerTask(fn)
    _scheduleBlockTimer.scheduleAtFixedRate(task, 0L, unit.toMillis(t))
    task
  }

}
