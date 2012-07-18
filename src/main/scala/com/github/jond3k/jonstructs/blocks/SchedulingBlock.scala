package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.{ScheduledExecutorService, Executors, Callable}
import com.github.jond3k.jonstructs.Logging

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */

trait SchedulingBlock extends Logging {

  /**
   * The executor service that schedules things to be run in future
   */
  def createSchedulingExecutor(): ScheduledExecutorService = Executors.newScheduledThreadPool(scheduledExecutorThreads)

  /**
   * The number of threads the executor service has. If this isn't high enough, jobs will be scheduled later than
   * expected or be started. Recommended use: 1 per block
   */
  def scheduledExecutorThreads: Int = 1

  /**
   *
   * @param t
   */
  def defaultScheduledErrorHandler(t: Throwable) {
    log.error("Unhandled exception in scheduled task", t)
  }

  /**
   * The executor service for scheduled events
   */
  lazy val _scheduledExecutorService = createSchedulingExecutor()

  /**
   * Encapsulates the function we run on a separate thread
   */
  protected class ScheduledCallable[A](fn: => A) extends Callable[A] {
    def call() = fn
  }

  /**
   * Encapsulates the procedure we run on a separate thread with optional error handler
   */
  protected class ScheduledRunnable(fn: => Unit, errorHandler: Option[Throwable => Unit]) extends Runnable {
    def run() {
      try {
        fn
      } catch {
        case ex: Exception =>
          errorHandler match {
            case Some(eh) => {
              try {
                eh.apply(ex)
              } catch {
                // catch exceptions in the error handler!
                case ex2: Throwable => defaultScheduledErrorHandler(ex2)
              }
            }
            // use the default handler if none is specified
            case None    => defaultScheduledErrorHandler(ex)
          }
      }
    }
  }
}