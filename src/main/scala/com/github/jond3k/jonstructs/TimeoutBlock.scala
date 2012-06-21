package com.github.jond3k.jonstructs

import java.util.concurrent._


/**
 * Helper that runs something on a separate thread with a maximum time bound.
 *
 * Blocks until the final code block returns, else times out and interrupts the long-running task. Might not work in
 * every case (endless loops, for example). It's all quite computationally expensive as it involves creating a new
 * thread so don't use it as a substitute for a real timeout
 *
 * @author jon
 */
trait TimeoutBlock {

  /**
   * The default timeout to use
   */
  private val defaultTimeout = 15000

  /**
   * Run something on a separate thread with a maximum time bound
   *
   * Useful for catching blocking IO operations
   *
   * @param run The code block
   * @tparam A  The result type
   * @return    The result of the code block
   */
  def timeout[A]()(run: => A): A = {
    apply(defaultTimeout)(run)
  }

  def throwableResponse(executor: ExecutorService, e: Throwable, termMs: Long): Throwable = {
    var r = e
    // regardless of what happens, we should shutdown the executor and send interrupts to anything that's running
    executor.shutdownNow()
    e match {
      // if an exception happened inside the task, bubble it up to the top
      case e: ExecutionException => {
        r = e.getCause
      }
      // if we timed out while waiting for the future, expect the interrupt to work
      case e: TimeoutException => {
        // if the task doesn't obey interrupts we have a problem
        if (!executor.awaitTermination(termMs, TimeUnit.MILLISECONDS)) {
          r = new RuntimeException("Failed to shut down correctly after timeout", e)
        }
      }
    }
    r
  }

  /**
   * Run something on a separate thread with a maximum time bound
   *
   * Useful for catching blocking IO operations
   *
   * @param ms  The length of the timeout
   * @param run The code block
   * @tparam A  The result type
   * @return    The result of the code block
   */
  def timeout[A](ms: Long)(run: => A): A = {
    val executor = Executors.newSingleThreadExecutor()
    val future = executor.submit(new Callable[A] {
      def call() = run
    })
    try {
      future.get(ms, TimeUnit.MILLISECONDS)
    }
    catch {
      case e: Throwable => throw throwableResponse(executor, e, ms)
    }
  }

  /**
   * Run something on a separate thread with a maximum time bound
   *
   * Useful for catching blocking IO operations
   *
   * @param obj The object we pass to the closure
   * @param ms  The length of the timeout
   * @param run The code block
   * @tparam A  The object we pass to the closure
   * @tparam B  The result type
   * @return    The result of the code block
   */
  def timeout[A, B](obj: A, ms: Long = defaultTimeout)(run: (A) => B): B = {
    val executor = Executors.newSingleThreadExecutor()
    val future = executor.submit(new Callable[B] {
      def call() = run(obj)
    })
    try {
      future.get(ms, TimeUnit.MILLISECONDS)
    }
    catch {
      case e: Throwable => throw throwableResponse(executor, e, ms)
    }
  }
}
