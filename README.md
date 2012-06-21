# jonstructs: Language constructs for readable, parallel Scala #

Featuring

* Run blocks
* Events

## Run Blocks ##

Run blocks give you temporal control over execution. Add to your class using _with TimeBlocks_.

### Do something at a scheduled time ###

    delayed 3 seconds {
        // code that gets run later
    }

Some code is scheduled to be run in 3 seconds.

### Do something at regular intervals ###

    every 5 seconds {
        // code that gets called regularly
    }

Some code will get called every 5 seconds.

### Do something until a timeout has elapsed ###

    given 3 seconds {
        // code that has a limited time to execute*
    }

You start running something straight away but wait for it to finish or time out. See the _tips_ section for its
limitations.

### Try, try again ###

    retry 3 times every 5 seconds {
        // code that is repeatedly run at regular intervals
    }

Run some code straight away. If it fails, try again a few times after a 5 second cool-off period. If it still hasn't
succeeded the last exception encountered will bubble up.

### Or if you prefer.. ###

    runDelayed(ms=60000) {..}
    runDelayed(1, TimeUnit.MINUTES) {..}

    runEvery(ms=1200) {..}
    runEvery(1200, TimeUnit.MILLISECONDS) {..}

    runGiven(ms=1200) {..}
    runGiven(1200000, TimeUnit.MICROSECONDS) {..}

These methods might be more comfortable for someone who doesn't want to have to learn another DSL

## Event Sources ##

### Invert control to decouple components ###

    val onNewConnection = new EventSource[MyUser]()
    while(running) {
        listenForUsers foreach(onNewConnection.emit(_))
    }

Don't call us, we'll call you

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

Sometimes the event we're observing is running a tight loop that we don't want to interfere with.

    def observerMayDropEvents = false

By default we will drop events if the queue gets too big. Set this to false to block the event source.

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
1. Throw an InterruptedException when a blocking I/O call has been woken up
1. Throw any other Exception as if it was a regular function call
1. Throw a TimeoutException if we have run out of time but the code block hasn't returned

A TimeoutException is thrown as a last resort. If this happens you regain control of the program flow but the code might
still be running. This can happen if you're using JNI IO libraries or are catching InterruptedExceptions.


