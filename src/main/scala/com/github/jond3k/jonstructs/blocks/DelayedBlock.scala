package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.{ScheduledFuture, TimeUnit}

/**
 *
 * @author Jonathan Davey <jond3k@gmail.com>
 */
trait DelayedBlock extends SchedulingBlock {

  /**
   * Schedule something to be run in the future
   */
  def delayed(ms: Long)(fn: => Unit): ScheduledFuture[_] = {
    delayed(ms, TimeUnit.MILLISECONDS)(fn)
  }

  /**
   * Schedule something to be run in the future with an error handler
   */
  def delayed(ms: Long, onError: Throwable => Unit)(fn: => Unit): ScheduledFuture[_] = {
    delayed(ms, TimeUnit.MILLISECONDS, onError)(fn)
  }

  /**
   * Schedule something to be run in the future with a specified time unit
   */
  def delayed(t: Long, unit: TimeUnit)(fn: => Unit): ScheduledFuture[_] = {
    delayed(t, unit, null)(fn)
  }

  /**
   * Schedule something to be run in the future with a specified time unit and error handler
   */
  def delayed(t: Long, unit: TimeUnit, onError: Throwable => Unit)(fn: => Unit): ScheduledFuture[_] = {
    val task = new ScheduledRunnable(fn, Option(onError))
    _scheduledExecutorService.schedule(task, t, unit)
  }
}
