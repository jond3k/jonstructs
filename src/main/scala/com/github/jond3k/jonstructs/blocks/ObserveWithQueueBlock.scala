package com.github.jond3k.jonstructs.blocks

import com.github.jond3k.jonstructs.events.{ObserverWithQueue, EventSource}
import collection.mutable
import java.util

/**
 * Like the regular observe block, this allows you to attach callbacks to EventSources. However, unlike the regular
 * ObserveBlock, these callbacks will not be executed by the thread that emits the event but instead are placed on a
 * queue which can be processed later.
 *
 * TODO: Investigate the possibility of bounding or blocking when this queue gets too big
 */
trait ObserveWithQueueBlock extends ObserveBlock {

  /**
   * The event queue
   */
  private val eventQueue = new mutable.SynchronizedQueue[() => Unit]

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
  def runEvents(max: Int = -1) {
    try {
        var remaining = max
        while (remaining != 0 && eventQueue.isEmpty == false) {
          // de-queue and run. we yield our sync lock after each unqueue to avoid deadlocks
          eventQueue.dequeue()()
          remaining = (remaining - 1).max(-1)
      }
    } catch {
      // a data race is feasible. deal with this gracefully
      case e: util.NoSuchElementException =>
    }
  }
}
