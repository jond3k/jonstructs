package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.TimeUnit
import java.util

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait DelayedBlock extends SchedulingBlock {

  /**
   *
   * @param ms
   * @param fn
   * @return
   */
  def delayed(ms: Long)(fn: => Unit): util.TimerTask = {
    delayed(ms, TimeUnit.MILLISECONDS)(fn)
  }

  /**
   *
   * @param t
   * @param unit
   * @param fn
   * @return
   */
  def delayed(t: Long, unit: TimeUnit)(fn: => Unit): util.TimerTask = {
    val task = new ScheduleBlockTimerTask(fn)
    _scheduleBlockTimer.schedule(task, unit.toMillis(t))
    task
  }
}
