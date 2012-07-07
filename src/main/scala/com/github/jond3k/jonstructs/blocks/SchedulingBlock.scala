package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.{TimeUnit, ThreadFactory, Executors}
import java.util.concurrent.atomic.AtomicInteger
import java.util

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */

trait SchedulingBlock {

  class ScheduleBlockTimerTask(fn: => Unit) extends util.TimerTask {
    def run() { fn }
  }

  protected lazy val _scheduleBlockTimer = new util.Timer(_scheduleBlockTimerThreadName, _scheduleBlockTimerIsDaemon)

  protected lazy val _scheduleBlockTimerThreadName = "jonstructs-" + SchedulingBlock.nextThreadID()

  protected def _scheduleBlockTimerIsDaemon = false
}

object SchedulingBlock {
  private val id = new AtomicInteger()
  def nextThreadID() = id.getAndIncrement
}