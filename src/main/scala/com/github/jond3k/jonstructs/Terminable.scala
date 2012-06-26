package com.github.jond3k.jonstructs

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
   * Should this task stop? Alias of 'terminating'
   *
   * @return
   */
  def stopping = terminating

  /**
   * Should this task continue to run?
   *
   * @return The inverse of terminating
   */
  def running = !terminating

  /**
   * Called once and only once when we correctly shift to the stopped state
   */
  def stopEvent() {

  }

  /**
   * Call to flip the termination flag to true
   */
  def terminate() {
    if (_terminating.compareAndSet(false, true)) {
      stopEvent()
      log.info("Stopping")
    }
  }

  /**
   * Alias for terminate()
   */
  def stop() {
    terminate()
  }

  /**
   * Wait for the termination flag to be set. Doesn't guarantee all underlying threads have cleaned up so you might
   * want to override this
   */
  def join() {
    while (running) {
      Thread.sleep(250)
    }
  }
}
