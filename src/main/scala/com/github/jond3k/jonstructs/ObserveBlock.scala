package com.github.jond3k.jonstructs

import com.github.jond3k.jonstructs.events.{Observer, EventSource}

/**
 * Adds syntactic sugar for subscribing to different EventSources
 */
trait ObserveBlock {
  /**
   * Observe an event source for updates
   *
   * @param es The event source to subscribe to
   * @param ev The event handler function
   * @tparam A The parameter type for the event handler
   * @return THe observer object. Can be used to unsubscribe
   */
  def observe[A](es: EventSource[A])(ev: A => Unit) = {
    val ob = new Observer(es, ev)
    ob.initialize()
    ob
  }
}
