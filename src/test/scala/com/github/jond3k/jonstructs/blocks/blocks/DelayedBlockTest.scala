package com.github.jond3k.jonstructs.blocks.blocks

import org.scalatest.FunSuite
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar
import com.github.jond3k.jonstructs.blocks.DelayedBlock

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */

class DelayedBlockTest
  extends FunSuite
  with MustMatchers
  with MockitoSugar
  with DelayedBlock {

  test("ensure delayed function is eventually called (non-deterministic)") {
    var b = false
    val timer = delayed(ms=10) {
      b = true
    }
    Thread.sleep(100)
    b must equal(true)
  }

  test("ensure delayed function can be cancelled (non-deterministic)") {
    var b = false
    val timer = delayed(ms=100) {
      b = true
    }
    timer.cancel(false)
    Thread.sleep(200)
    b must equal(false)
  }

  test("ensure we call default exception handler with each fail") {
    val expected = new IllegalArgumentException("test exception")
    var actual: Throwable = null

    new DelayedBlock {
      override def defaultScheduledErrorHandler(t: Throwable) { actual = t }
      val timer = delayed(ms=10) {
        throw expected
      }
      Thread.sleep(200)
    }

    actual must equal(expected)
  }

  test("ensure we call custom exception handlers with each fail, if provided") {
    val expected = new IllegalArgumentException("test exception")
    var actual: Throwable = null
    delayed(ms=10, onError=t => actual = t) {
      throw expected
    }
    Thread.sleep(200)
    actual must not be(null)
    actual must equal(expected)
  }

  test("ensure we call the default exception handler if the error handler fails (just to be thorough ;-)") {
    val expected = new IllegalArgumentException("test exception")
    var actual: Throwable = null

    new DelayedBlock {
      override def defaultScheduledErrorHandler(t: Throwable) { actual = t }
      val timer = delayed(ms=10, onError=t => throw expected) {
        throw new NoSuchElementException("not expected")
      }
      Thread.sleep(200)
    }

    actual must equal(expected)
  }
}
