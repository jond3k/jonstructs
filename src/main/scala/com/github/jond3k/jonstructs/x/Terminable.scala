package com.github.jond3k.jonstructs

import java.util.concurrent.atomic.AtomicBoolean
import com.github.jond3k.jonstructs.Logging

trait Terminable extends Logging {

  /**
   * The underlying graceful termination flag
   */
  private val _terminating = new AtomicBoolean(false)

  /**
   * Should this task terminate?
   *
   * @return Returns true if the task should terminate
   */
  def terminating = _terminating.get()

  /**
   * Should this task continue to run?
   *
   * @return The inverse of terminating
   */
  def running = !terminating

  /**
   * Call to flip the termination flag to true
   */
  def terminate() {
    if (_terminating.compareAndSet(false, true)) {
      _terminating.notifyAll()
      log.info("Stopping")
    }
  }

  /**
   * Alias for terminate()
   */
  def stop() {
    terminate()
  }

  def join() {
    while(running) {
      _terminating.wait()
    }
  }
}
