package com.github.jond3k.jonstructs.events

/**
 * Encapsulates the bi-directional Subject->Observer relationship.
 *
 * @param es The source of the event in the relationship
 * @param ev The event handler
 * @tparam A The parameter passed to the event handler
 */
class Observer[A](es: EventSource[A], ev: A => Unit) {
  /**
   * Handle an update
   *
   * @param msg The message to be passed to the event handler
   */
  def update(msg: A) {
    ev(msg)
  }

  /**
   * Create the Subject->Observer relationship
   */
  def initialize() {
    es.subscribe(this)
  }

  /**
   * Unsubscribe from this event source, breaking the Subject->Observer relationship
   */
  def dispose() {
    es.unsubscribe(this)
  }
}
