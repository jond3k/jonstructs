package com.github.jond3k.jonstructs.blocks

import com.github.jond3k.jonstructs.Logging

/**
 *
 * @author Jonathan Davey <jon.davey@datasift.com>
 */
class SchedulingErrorsSyntax extends EveryBlock with DelayedBlock with RetryBlock with Logging {

  def myErrorHandler(t: Throwable) {}

  every(ms=30, onError=myErrorHandler(_)) {
    throw new Exception("Buuurn")
  }

  delayed(ms=30, onError=log.error("We seem to have some kind of problem!", _)) {
    throw new Exception("Zing")
  }

  retry(retries=10, ms=20, onError=log.error("We will try to recover from the problem", _)) {
    throw new Exception("Zing")
  }

  override def defaultScheduledErrorHandler(t: Throwable) {
    // do something!
  }

}
