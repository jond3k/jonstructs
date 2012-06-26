package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.{TimeUnit, ThreadFactory, Executors}

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */

trait BlockScheduling {

  /**
   * Encapsulates the function we want to call later on
   *
   * @param fn
   */
  /*class RunBlockRunnable(fn: => Unit) extends Runnable {
    def run() {
      fn
    }
  }

  /**
   * The size of the run block pool size
   *
   * @return The number of threads to use
   */
  def runBlockCorePoolSize: Int = 1

  /**
   * The thing the creates our threads. Replace this if you want to pool threads
   *
   * @return The thing that creates our threads
   */
  def runBlockThreadFactory: ThreadFactory = null

  /**
   *
   * @return
   */
  def createRunBlockScheduledThreadPool() = Executors.newScheduledThreadPool(runBlockCorePoolSize, runBlockThreadFactory)

  /**
   *
   */
  lazy val runBlockScheduler = createRunBlockScheduledThreadPool()

  /**
   *
   * @param fn
   * @param t
   * @param unit
   */
  def runBlockSchedule(fn: => Unit, t: Long, unit: TimeUnit) {
    runBlockScheduler.schedule(new RunBlockRunnable(fn), t, unit)
  }*/
}
