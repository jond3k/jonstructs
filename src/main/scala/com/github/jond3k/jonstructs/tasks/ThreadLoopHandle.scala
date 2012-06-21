package com.github.jond3k.jonstructs.tasks

import com.mediasift.push.util.{Logging, Terminable}
import java.util.concurrent.{Future, TimeUnit, Executors}

class ThreadLoopHandle(action: () => Unit, sleepMs: Long, deferred: Boolean)
  extends Terminable
  with Logging {
  val executor = Executors.newSingleThreadExecutor()

  var future = Option.empty[Future[_]]
  val task = new Runnable {
    def run() {
      interval()
      while (running) {
        action()
        log.info("Ran loop for {}", action.getClass)
        interval()
      }
      log.info("Stopped loop for {}", action.getClass)
    }
  }

  if (!deferred) {
    start()
  }

  def start() {
    if (future.isDefined) {
      throw new RuntimeException("Already started. Set deferred=true if you want to start manually")
    }
    future = Option(executor.submit(task))
    log.info("Started loop for {} every {}", action.getClass, sleepMs)
  }

  protected def interval() {
    if (sleepMs > 0) {
      Thread.sleep(sleepMs)
    }
  }

  override def stop() {
    super.stop()
    executor.shutdownNow()
    log.info("Stopping loop for {}", action.getClass)
  }

  def stopAndWait() {
    stop()
    future.foreach(_.get())
  }

  def stopAndWait(timeout: Long, unit: TimeUnit) {
    stop()
    future.foreach(_.get(timeout, unit))
  }
}

