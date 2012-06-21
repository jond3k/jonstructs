jonstructs

A library of useful Scala language constructs

* Run blocks
* Event sources

Run Blocks

* Run at a scheduled time

run in 3 seconds {

}

* Run at regular intervals

run every 5 seconds {

}

* Run until a timeout has elapsed

run until 3 seconds {

}

* Retry an operation a number of times

retry 3 times every 5 seconds {

}

* A simpler interface, if you prefer

runEvery(ms=1200) {..}
runIn(ms=1200) {..}
runUntil(ms=1200) {..}

* You can still specify time units

runEvery(12, TimeUnit.MINUTES) {..}
runIn(12, TimeUnit.MINUTES) {..}
runUntil(12, TimeUnit.MINUTES) {..}

Event Sources

* Invert control to decouple components

val onNewConnection = new EventSource[MyUser]()
while(running) {
  listenForUsers foreach onNewConnection.emit(_)
}

* React to events

class MyUserGreeter(listener: MyUserListener) with ObserveBlock {
  observe(listener.onNewConnection) { user =>
    user.send("Hello %s!" format user.name)
  }
}

* React to events, let queued callbacks run in our own thread

class MyUserGreeter(listener: MyUserListener) with ObserveWithQueueBlock with Terminable with Runnable {
  observe(listener.onNewConnection) { user =>
    user.send("Hello %s!" format user.name)
  }
  def run() {
    while(running) {
      handleEvents()
    }
  }
}

Tips

The new constructs make no happens-before guarantees.

# Only share immutable data
# Ensure shared mutable primitives are volatile
# Ensure shared objects are synchronized

e.g.

run until 3 seconds {
  synchronize {
    slowMethod()
  }
}
synchronize {
  checkSlowMethodResults()
}




