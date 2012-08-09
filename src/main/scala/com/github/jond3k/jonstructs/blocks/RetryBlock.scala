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
  def retry[A](ms: Long, onError: Throwable => Unit)(fn: => A): A = {
    retry(-1, ms, onError)(fn)
  }

  /**
   * Retry a block of code infinitely with an interval (arbitrary unit)
   *
   * @param every The number of time units to wait before retrying
   * @param unit  The time unit to use
   * @param fn    The function to call
   */
  def retry[A](every: Long, unit: TimeUnit, onError: Throwable => Unit)(fn: => A): A = {
    retry(-1, every, unit, onError)(fn)
  }

  /**
   * Retry a block of code multiple times at set intervals (in ms)
   *
   * @param retries
   * @param ms
   * @param fn
   */
  def retry[A](retries: Long, ms: Long, onError: Throwable => Unit)(fn: => A): A = {
    retry(retries, ms, TimeUnit.MILLISECONDS, onError)(fn)
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
  final def retry[A](
                      retries: Long,
                      every: Long,
                      unit: TimeUnit,
                      onError: Throwable => Unit = _defaultRetryLogger)(fn: => A): A = {
    try {
      return fn
    } catch {
      case e if retries == -1 => onError(e)
      case e if retries >   0 => onError(e)
      // otherwise, bubble up
    }
    Thread.sleep(unit.toMillis(every))
    val next = (retries - 1).max(-1)
    retry(next, every, unit, onError)(fn)
  }

  protected def _defaultRetryLogger(e: Throwable) {
    log.error("Retrying", e)
  }
}
