package com.github.jond3k.jonstructs.x

import com.github.jond3k.jonstructs.x.Logging
import com.github.jond3k.jonstructs.Logging

/**
 * Allows you to retry calling a method block
 */
trait RetryBlock extends Logging {

  class RetryResult() {
    def or(ex: Exception => Unit) {
    }
  }

  class RetrySuccess() extends RetryResult

  class RetryFail(ex: Exception) extends RetryResult {
    assert(ex != null)

    override def or(action: Exception => Unit) {
      assert(action != null)
      action(ex)
    }
  }

  def retry(retries: Int, retryIntervalMs: Long, bubbleOnFail: Boolean = true)(action: => Unit): RetryResult = {
    assert(retries > 0)
    assert(retryIntervalMs > 0)
    var exception: Exception = null
    var remaining = retries
    while (remaining > 0) {
      try {
        action
        remaining = 0
      }
      catch {
        case e: Exception => {
          remaining = remaining - 1
          log.error("%s retries remaining" format remaining, e)
          if (remaining > 0) {
            Thread.sleep(retryIntervalMs)
          } else if (!bubbleOnFail) {
            exception = e
          } else {
            throw e
          }
        }
      }
    }

    Option(exception) match {
      case Some(e) => new RetryFail(e)
      case None => new RetrySuccess()
    }
  }
}
