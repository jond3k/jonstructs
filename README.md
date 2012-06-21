# jonstructs: Useful language constructs for distributed Scala #

Featuring

* Run blocks
* Events

## Run Blocks ##

### Run at a scheduled time ###

    run in 3 seconds {
        // code that gets run soon
    }

### Run at regular intervals ###

    run every 5 seconds {
        // code that gets called regularly
    }

### Run until a timeout has elapsed ###

    run until 3 seconds {
        // code that has a limited time. see the FAQ
    }

### Retry an operation a number of times ###

    retry 3 times every 5 seconds {
        // code that is repeatedly run at regular intervals
    }

### Or, a simpler interface ###

    runEvery(ms=1200) {..}

    runIn(ms=1200) {..}

    runUntil(ms=1200) {..}

### Java-style ###

    runEvery(1200, TimeUnit.MILLISECONDS) {..}

    runIn(12, TimeUnit.MINUTES) {..}

    runUntil(100000, TimeUnit.MICROSECONDS) {..}

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

1. Only share immutable data
1. Ensure shared mutable primitives are volatile
1. Ensure shared objects are synchronized

e.g.

    run until 3 seconds {
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




