# jonstructs: Language constructs for readable, parallel Scala #

Featuring

* Run blocks
* Events

## Run Blocks ##

Run blocks give you temporal control over execution

### Do something at a scheduled time ###

    delayed 3 seconds {
        // code that gets run soon
    }

### Do something at regular intervals ###

    every 5 seconds {
        // code that gets called regularly
    }

### Do something until a timeout has elapsed ###

    given 3 seconds {
        // code that has a limited time to execute*
    }

### Try, try again ###

    retry 3 times every 5 seconds {
        // code that is repeatedly run at regular intervals
    }

### Or if you prefer.. ###

    runIn(ms=1200) {..}
    runIn(12, TimeUnit.MINUTES) {..}

    runEvery(ms=1200) {..}
    runEvery(1200, TimeUnit.MILLISECONDS) {..}

    runGiven(ms=1200) {..}
    runGiven(100000, TimeUnit.MICROSECONDS) {..}

## Event Sources ##

### Invert control to decouple components ###

    val onNewConnection = new EventSource[MyUser]()
    while(running) {
        listenForUsers foreach onNewConnection.emit(_)
    }

### React to events ###

    class MyUserGreeter(listener: MyUserListener) with ObserveBlock {
        observe(listener.onNewConnection) { user =>
            user.send("Hello %s!" format user.name)
        }
    }

### React to events, let queued callbacks run in our own thread ###

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

## Tips ##

### The new constructs have no happens-before guarantees ###

So

* Only share immutable data
* OR ensure shared mutable primitives are volatile or atomic
* OR ensure shared objects are synchronized

e.g.

    given 3 seconds {
        synchronize {
            slowMethod()
        }
    }
    synchronize {
        checkSlowMethodResults()
    }

### The timeout block will do one of the following ###

1. Return the result of the code block
1. Throw a TimeoutException if we have run out of time but the code block hasn't returned
1. Throw an InterruptedException when a blocking I/O call has been woken up
1. Throw any other Exception as if it was a regular function call




