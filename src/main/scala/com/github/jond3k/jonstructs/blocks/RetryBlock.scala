package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.TimeUnit
import com.github.jond3k.jonstructs.Logging
import annotation.tailrec

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait RetryBlock extends Logging {

  /**
   * Retry a block of code infinitely with an interval (in ms)
   *
   * @param ms The number of milliseconds to wait between each retry
   * @param fn The function to call
   */
  def retry(ms: Long)(fn: => Unit) {
    retry(-1, ms)(fn)
  }

  /**
   * Retry a block of code infinitely with an interval (arbitrary unit)
   *
   * @param every The number of time units to wait before retrying
   * @param unit  The time unit to use
   * @param fn    The function to call
   */
  def retry[A](every: Long, unit: TimeUnit)(fn: => A): A = {
    retry(-1, every, unit)(fn)
  }

  /**
   * Retry a block of code multiple times at set intervals (in ms)
   *
   * @param times
   * @param ms
   * @param fn
   */
  def retry(times: Long, ms: Long)(fn: => Unit) {
    retry(times, ms, TimeUnit.MILLISECONDS)(fn)
  }

  /**
   * Retry a block of code multiple times at set intervals (arbitrary unit)
   *
   * @param times The number of times to retry. -1 for infinite
   * @param every The number of time units to wait before retrying
   * @param unit  The time unit to use for 'every'
   * @param fn    The function to call
   */
  @tailrec
  final def retry[A](times: Long, every: Long, unit: TimeUnit)(fn: => A): A = {
    try {
      return fn
    } catch {
      case e if times == -1 => log.error("Retrying", e)
      case e if times > 0 => log.error("Retrying. %s remaining" format times - 1, e)
    }
    Thread.sleep(unit.toMillis(every))
    val next = (times - 1).max(-1)
    retry(next, every, unit)(fn)
  }
}
