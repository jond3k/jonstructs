package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.{ScheduledFuture, TimeUnit}

/**
 *
 * @author Jonathan Davey <jond3k@gmail.com>
 */
trait EveryBlock extends SchedulingBlock {

  /**
   * Schedule something to be run at regular intervals
   */
  def every(ms: Long)(fn: => Unit): ScheduledFuture[_] = {
    every(ms, TimeUnit.MILLISECONDS)(fn)
  }

  /**
   * Schedule something to be run at regular intervals, passed in an error handler
   */
  def every(ms: Long, onError: (Throwable) => Unit)(fn: => Unit): ScheduledFuture[_] = {
    every(ms, TimeUnit.MILLISECONDS, onError)(fn)
  }

  /**
   * Schedule something to be run at regular intervals with a specific time unit
   */
  def every(t: Long, unit: TimeUnit)(fn: => Unit): ScheduledFuture[_] = {
    every(t, unit, null)(fn)
  }

  /**
   * Schedule something to be run at regular intervals with a specific time unit and error handler
   */
  def every(t: Long, unit: TimeUnit, onError: Throwable => Unit)(fn: => Unit): ScheduledFuture[_] = {
    val task = new ScheduledRunnable(fn, Option(onError))
    _scheduledExecutorService.scheduleAtFixedRate(task, 0L, t, unit)
  }

}
