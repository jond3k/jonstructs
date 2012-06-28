# jonstructs: Language constructs for readable Scala #

## Getting started ##

Rather than solving the same problems over and over again I decided to write a library that does it all for me. See the
bottom of the page for information on adding as Maven dependency or just check the code out.

You'll get

* Events - Respond to state changes on other objects with callbacks
* Scheduling - Gives you temporal control over your code

## Events ##

Taken from the paper "Deprecating the Observer Pattern" (Maier et al) we have a neat way to raise and respond to events

### Invert control to decouple components ###

    val onNewConnection = new EventSource[MyUser]()
    while(running) {
      for(user <- listenForUsers) {
        onNewConnection.raise(user)
      }
    }

In this example, for every new user, raise an "onNewConnection" event. It doesn't do anything yet, but if someone
observes it it might

### React to events ###

    class MyUserGreeter(listener: MyUserListener) extends ObserveBlock {
        observe(listener.onNewConnection) { user =>
            user.send("Hello %s!" format user.name)
        }
    }

We can listen to the "onNewConnection" event defined in the previous example and respond to it.

### React to events, let queued callbacks run in our own thread ###

    class MyUserGreeter(listener: MyUserListener) extends ObserveWithQueueBlock with Terminable with Runnable {
        observe(listener.onNewConnection) { user =>
            // This IO operation might take 2 seconds
            user.send("Hello %s!" format user.name)
        }
        def run() {
            while(running) {
                runEvents()
            }
        }
    }

Sometimes the event we're observing is running a tight loop that we don't want to interfere with. You can instead have
your events pushed to a queue so you can run them in your own thread.

    def observerQueueBlocks = true
    def observerQueueSize   = 200

By default we will queue an unlimited number of events. You can instead set a queue capacity and decide to block or drop
when it's full.

## Scheduling ##

Scheduling blocks give you temporal control over execution. Add the _SchedulingBlocks_ trait to your class.

### Do something in the future ###

    delayed(ms=3000) {
      // code that gets run later
    }

Some code is scheduled to be run in 3 seconds.

### Do something at regular intervals ###

    every(interval=5, TimeUnit.SECONDS) {
      // code that gets called regularly
    }

Some code will get called every 5 seconds.

### Do something until a timeout has elapsed ###

    timeout(ms=5000) {
      // code that has a limited time to execute*
    }

You start running something straight away but wait for it to finish or time out. See the _tips_ section for its
limitations.

### Try, try again ###

    retry(times=3, every=5, unit=TimeUnit.SECONDS) {
      // code that is repeatedly run at regular intervals
    }

Run some code straight away. If it fails, try again a few times after a 5 second cool-off period. If it still hasn't
succeeded after 3 tries the last exception encountered will bubble up.

## Tips ##

### The new constructs have no happens-before guarantees ###

Therefore,

* Only share immutable data
* OR ensure shared mutable primitives are volatile or atomic
* OR ensure shared objects are synchronized

e.g.

    timeout(ms=3000) {
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
1. Throw a TimeoutException if the code took longer than expected. It has been asked to terminate but might still be
   running.

A TimeoutException is thrown as a last resort. If this happens you regain control of the program flow but the code might
still be running. This can happen if you're using JNI IO libraries like JZMQ or are catching InterruptedExceptions.


### How do I use this from Maven? ###

You can pull this dependency from Maven Central

    <dependency>
        <groupId>com.github.jond3k</groupId>
        <artifactId>jonstructs</artifactId>
        <version>0.3</version>
    </dependency>

Enjoy!

