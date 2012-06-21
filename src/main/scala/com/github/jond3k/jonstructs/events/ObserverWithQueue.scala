package com.github.jond3k.jonstructs.events

import collection.mutable.SynchronizedQueue

class ObserverWithQueue[A](q: SynchronizedQueue[() => Unit], es: EventSource[A], ev: A => Unit) extends Observer[A](es, ev) {
  /**
   * Handle an update. Defers it for later processing from the queue
   *
   * @param msg The message to be passed to the event handler
   */
  override def update(msg: A) {
    // enclose the event and message in a closure that can be executed later
    q.enqueue(() => {
      ev(msg)
    })
  }

}
