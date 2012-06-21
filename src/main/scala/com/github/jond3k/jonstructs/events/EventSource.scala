package com.github.jond3k.jonstructs.events

import collection.immutable.HashSet

/**
 * Represents the subject in the subject->observer relationship
 *
 * @tparam A The first parameter passed to an event handler function
 */
class EventSource[A] {
  /**
   * A list of observers
   */
  var obs = new HashSet[Observer[A]]()

  /**
   * Add a new observer
   *
   * @param ob
   */
  def subscribe(ob: Observer[A]) {
    synchronized {
      obs = obs + ob
    }
  }

  /**
   * Remove an existing observer
   *
   * @param ob
   */
  def unsubscribe(ob: Observer[A]) {
    synchronized {
      obs = obs - ob
    }
  }

  /**
   * Raise an event. This will notify all observers
   *
   * @param m
   */
  def raise(m: A) {
    synchronized {
      obs.foreach(_.update(m))
    }
  }
}
