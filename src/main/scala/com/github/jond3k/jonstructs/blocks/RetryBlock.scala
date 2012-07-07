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
  def retry[A](ms: Long)(fn: => A): A = {
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
   * @param retries
   * @param ms
   * @param fn
   */
  def retry[A](retries: Long, ms: Long)(fn: => A): A = {
    retry(retries, ms, TimeUnit.MILLISECONDS)(fn)
  }

  /**
   * Retry a block of code infinitely with an interval (in ms)
   *
   * @param ms The number of milliseconds to wait between each retry
   * @param fn The function to call
   */
  def retry[A](ms: Long, silent: Boolean)(fn: => A): A = {
    retry(-1, ms, silent)(fn)
  }

  /**
   * Retry a block of code infinitely with an interval (arbitrary unit)
   *
   * @param every The number of time units to wait before retrying
   * @param unit  The time unit to use
   * @param fn    The function to call
   */
  def retry[A](every: Long, unit: TimeUnit, silent: Boolean)(fn: => A): A = {
    retry(-1, every, unit, silent)(fn)
  }

  /**
   * Retry a block of code multiple times at set intervals (in ms)
   *
   * @param retries
   * @param ms
   * @param fn
   */
  def retry[A](retries: Long, ms: Long, silent: Boolean)(fn: => A): A = {
    retry(retries, ms, TimeUnit.MILLISECONDS, silent)(fn)
  }

  /**
   * Retry a block of code multiple times at set intervals (arbitrary unit)
   *
   * @param retries The number of times to retry. -1 for infinite
   * @param every   The number of time units to wait before retrying
   * @param unit    The time unit to use for 'every'
   * @param fn      The function to call
   */
  @tailrec
  final def retry[A](retries: Long, every: Long, unit: TimeUnit, silent: Boolean = false)(fn: => A): A = {

    try {
      return fn
    } catch {
      case e if !silent && retries == -1 => _retryLogger(_retryString(retries), e)
      case e if !silent && retries > 0   => _retryLogger(_retryString(retries), e)
    }
    Thread.sleep(unit.toMillis(every))
    val next = (retries - 1).max(-1)
    retry(next, every, unit, silent)(fn)
  }

  protected def _retryString(remaining: Long) = "Retrying. %s remaining" format remaining

  protected var _retryLogger: (String, Throwable) => Unit = log.error
}
