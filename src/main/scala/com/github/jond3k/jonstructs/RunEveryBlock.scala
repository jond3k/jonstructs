package com.github.jond3k.jonstructs

import tasks.ThreadLoopHandle
import com.github.jond3k.jonstructs.tasks.ThreadLoopHandle

trait RunEveryBlock {
  def runEvery(intervalMs: Long, deferred: Boolean = false)(action: => Unit): ThreadLoopHandle = {
    // TODO: use Timer instead
    new ThreadLoopHandle({
      () => action
    }, intervalMs, deferred)
  }
}
