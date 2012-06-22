package com.github.jond3k.jonstructs.x

import java.util.concurrent.atomic.AtomicBoolean

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
    _terminating.set(true)
    log.info("Stopping")
  }

  /**
   * Alias for terminate()
   */
  def stop() {
    terminate()
  }
}
