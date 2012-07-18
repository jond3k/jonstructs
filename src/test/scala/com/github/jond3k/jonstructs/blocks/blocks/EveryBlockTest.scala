package com.github.jond3k.jonstructs.blocks.blocks

import org.scalatest.FunSuite
import com.github.jond3k.jonstructs.blocks.EveryBlock
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */

class EveryBlockTest
  extends FunSuite
  with MustMatchers
  with MockitoSugar
  with EveryBlock {

  test("ensure we fire at regular intervals") {
    var i = 0
    val timer = every(ms=10) {
      i = i + 1
    }
    Thread.sleep(200)
    timer.cancel(false)
    i must be >= 10
  }

  test("ensure timed function can be cancelled") {
    var i = 0
    val timer = every(ms=10) {
      i = i + 1
    }
    Thread.sleep(50)
    timer.cancel(false)
    Thread.sleep(100)
    i must be >  0
    i must be <= 10
  }

  test("ensure we call default exception handler with each fail") {
    val expected = new IllegalArgumentException("test exception")
    var actual: Throwable = null

    new EveryBlock {
      override def defaultScheduledErrorHandler(t: Throwable) { actual = t }
      val timer = every(ms=10) {
        throw expected
      }
      Thread.sleep(200)
      timer.cancel(false)
    }

    actual must equal(expected)
  }

  test("ensure we call custom exception handlers with each fail, if provided") {
    val expected = new IllegalArgumentException("test exception")
    var actual: List[Throwable] = List()
    val timer = every(ms=10, onError=t => actual = actual :+ t) {
      throw expected
    }
    Thread.sleep(200)
    timer.cancel(false)
    actual must not be(null)
    actual.length must be >= 10
    actual.foreach(_ must equal(expected))
  }

  test("ensure we call the default exception handler if the error handler fails (just to be thorough ;-)") {
    val expected = new IllegalArgumentException("test exception")
    var actual: Throwable = null

    new EveryBlock {
      override def defaultScheduledErrorHandler(t: Throwable) { actual = t }
      val timer = every(ms=10, onError=t => throw expected) {
        throw new NoSuchElementException("not expected")
      }
      Thread.sleep(200)
      timer.cancel(false)
    }

    actual must equal(expected)
  }

}
