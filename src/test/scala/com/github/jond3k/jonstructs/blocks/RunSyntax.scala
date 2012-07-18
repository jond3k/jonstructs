package com.github.jond3k.jonstructs.blocks

import java.util.concurrent.TimeUnit

/**
 * This class isn't an executable specification, it just ensures the DSL syntax can be parsed by the compiler
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
class RunSyntax extends RunBlocks {

 delayed(ms=300) {
    // code that gets run later
 }

  every(5, TimeUnit.SECONDS) {
    // code that gets called regularly
  }

  timeout(ms=300) {
    // code that has a limited time to execute*
  }

  retry(ms=50) {
    // code that we try to run infinitely until it succeeds, retrying every 50ms
  }

  retry(retries=3, every=5, unit=TimeUnit.SECONDS) {
    // code that is repeatedly run at regular intervals
  }

  val running = true
  val input = new Object{ def close() {}; def nextMessage() = {"result"} }
  def processMessage(s: String) {}

  swallow(log.error) {
    // code that has its exceptions logged but which don't bubble up
  }

  swallow(log.error)(input.close)

  while(running) {
    swallow(log.error) {
      input.nextMessage()
    } match {
      case Some(m) => processMessage(m)
      case None    =>
    }
  }

  val action = every(5, TimeUnit.SECONDS) {
    // code that gets called regularly unless we ask it to stop
  }
  action.cancel(false)

  val action2 = delayed(5, TimeUnit.SECONDS) {
    // code that gets called later unless we ask it to stop
  }
  action2.cancel(false)

  var result = retry(ms=50) {
    // code that we try to run infinitely until it succeeds and returns us a value
  }

}
