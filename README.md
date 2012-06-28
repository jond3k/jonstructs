# jonstructs: Language constructs for readable Scala #

## Getting started ##

Rather than solving the same problems over and over again I decided to write a library that does it all for me. See the
bottom of the page for information on adding as Maven dependency or just check the code out.

You'll get

* Events - Respond to state changes on other objects with callbacks
* Scheduling - Gives you temporal control over your code
* Helpers - Making it easier to follow best practices

## Events ##

Taken from the paper "Deprecating the Observer Pattern" (Maier et al) we have a neat way to raise and respond to events

### Invert control to decouple components ###

People can register callbacks to be run when an _EventSource_ is triggered.

    val onNewConnection = new EventSource[MyUser]()
    while(running) {
      for(user <- listenForUsers) {
        onNewConnection.raise(user)
      }
    }

In this example, for every new user that connects, raise an _onNewConnection_ event. It doesn't do anything yet, but if
someone observes the event it it might.

### React to events ###

Providing you have a reference to the event source, you can respond to it with a callback you register with help of the
_ObserveBlock_ trait.

    class MyUserGreeter(listener: MyUserListener) extends ObserveBlock {
        observe(listener.onNewConnection) { user =>
            user.send("Hello %s!" format user.name)
        }
    }

We can listen to the _onNewConnection_ event defined in the previous example and respond to it.

### React to events, let queued callbacks run in our own thread ###

Sometimes the event we're observing is running a tight loop that we don't want to interfere with. You can instead have
your events pushed to a queue so you can run them in your own thread with help of the _ObserveWithQueueBlock_

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

In the above example, we have a _MyUserGreeter_ that can be run on a separate thread. When it initializes it subscribes
to an event. The main loop puts all of its effort in to handling these events, taking any load from the thread that
raises the event.

You can also get tighter control over the queue

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

## Helpers ##

Quite often there are things you'd like to do to make your code better but putting the basic plumbing in place would
make it less clean. These helpers aim to give you the best of both worlds.

### Finding free ports ###

When fake services bind to ports in integration tests there's a danger the port will already be in use. Furthermore,
if you try to use a port in a TIME_WAIT state you might end up polluting the service with stale data from elsewhere.

    val port = findFreePort()
    val zk   = new FakeZooKeeperServer(port)

Mixing in the _SocketHelper_ allows you to find free ports.

### Wait for servers to start ###

When integration testing, starting background services can lead to race conditions. Using _Thread.sleep_ will often work
but adds an element of non-determinism that can catch you out on slower hardware or during different load patterns.

    waitForServer(zk.port)

The _SocketHelper_ also allows you to wait for a port to be bound. It isn't a substitute for proper handshaking but will
work in many cases.

### Creating isolated test directories ###

Another problem integration tests can face is isolating temporary data. The _DirectoryHelper_ helps with this.

    val datadir = newTempDir()
    val kafka   = new FakeKafkaBroker(datadir)

A new test directory is created under /tmp/. This will be freed when the JVM terminates gracefully. If it crashes, the
folder will still be around to allow easier debugging.

### HTTP requests ###

There are a lot of different ways to make HTTP requests but most come with a learning curve and require a lot of
boilerplate code.

    val (status, body) = request("http://google.com")
    status must equal(200)

_HttpHelper_ doesn't cover many use cases but gives you a simple way of interrogating REST services in a CURL-like way.

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

Use this dependency for the bleeding edge

    <dependency>
        <groupId>com.github.jond3k</groupId>
        <artifactId>jonstructs</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>

and this repository

    <distributionManagement>
        <snapshotRepository>
            <id>snapshot-repo</id>
            <url>https://github.com/jond3k/jond3k-mvn-repo/raw/master/snapshots</url>
        </snapshotRepository>
    </distributionManagement>

I should probably but this in sonatype or something.