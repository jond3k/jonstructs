package com.github.jond3k.jonstructs.x

import collection.mutable.SynchronizedQueue
import java.util.NoSuchElementException
import com.github.jond3k.jonstructs.events.{ObserverWithQueue, EventSource}

/**
 *
 */
trait ObserveWithQueueBlock extends ObserveBlock {

  private val eventQueue = new SynchronizedQueue[() => Unit]

  /**
   * Observe an event source for updates
   *
   * @param es The event source to subscribe to
   * @param ev The event handler function
   * @tparam A The parameter type for the event handler
   * @return The observer object. Can be used to unsubscribe
   */
  override def observe[A](es: EventSource[A])(ev: A => Unit) = {
    val ob = new ObserverWithQueue(eventQueue, es, ev)
    ob.initialize()
    ob
  }

  /**
   * Execute any queued observer events
   */
  def handleEvents() {
    try {
      while (!eventQueue.isEmpty) {
        // de-queue and run. we yield our sync lock after each unqueue to avoid deadlocks
        eventQueue.dequeue()()
      }
    } catch {
      // a data race is feasible. deal with this gracefully
      case e: NoSuchElementException =>
    }
  }
}
