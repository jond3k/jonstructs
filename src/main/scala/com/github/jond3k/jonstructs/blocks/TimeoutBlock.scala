package com.github.jond3k.jonstructs.blocks

import java.util.concurrent._

/**
 * Helper that runs something on a separate thread with a maximum time bound.
 *
 * Blocks until the final code block returns, else times out and interrupts the long-running task. Might not work in
 * every case (endless loops, for example). It's all quite computationally expensive as it involves creating a new
 * thread so don't use it as a substitute for a real timeout
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait TimeoutBlock {

  /**
   * If a task has taken too long we will sent it a signal to stop. If it still hasn't finished after
   *
   * @return
   */
  protected def timeoutWaitForThreadMs = 250

  /**
   * Encapsulates the task we run on a separate thread
   *
   * @param fn The block passed to us by the user
   * @tparam A The return type of the block passed to us by the user
   */
  protected class TimeoutCallable[A](fn: => A) extends Callable[A] {
    def call() = fn
  }

  /**
   * Run a block of code on a separate thread, allowing it a fixed amount of time to run (in ms)
   *
   * @param ms The number of ms to allow
   * @param fn The function to call
   * @return   The result of the function call
   * @throws     java.lang.RuntimeException            If the function failed to respond after being asked to
   *                                                   terminate
   * @throws     java.util.concurrent.TimeoutException If the function failed to run in the allowed amount of time
   * @throws     java.lang.Exception                   Any underlying exceptions, including InterruptedExceptions from
   *                                                   blocking IO calls
   */
  def timeout[A](ms: Long)(fn: => A): A = {
    timeout(ms, TimeUnit.MILLISECONDS)(fn)
  }

  /**
   * Run a block of code on a separate thread, allowing it a fixed amount of time to run (arbitrary time unit)
   *
   * @param in   The number of time units to allow
   * @param unit The time unit to use
   * @param fn   The function to call
   * @return     The result of the function call
   * @throws     java.util.concurrent.TimeoutException If the function failed to run in the allowed amount of time
   * @throws     java.lang.Exception                   Any underlying exceptions, including InterruptedExceptions from
   *                                                   blocking IO calls
   */
  def timeout[A](in: Long, unit: TimeUnit)(fn: => A): A = {
    val executor = Executors.newSingleThreadExecutor()
    val future   = executor.submit(new TimeoutCallable(fn))
    try {
      try {
        future.get(in, unit)
      }
      finally {
        // regardless of what happens, we should shutdown the executor and send interrupts to anything that's running
        executor.shutdownNow()
      }
    }
    catch {
      // extract the underlying exception if the call failed
      case e: ExecutionException => throw e.getCause
    }
  }
}
