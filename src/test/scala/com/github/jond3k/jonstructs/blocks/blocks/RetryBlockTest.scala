package com.github.jond3k.jonstructs.blocks.blocks

import org.scalatest.FunSuite
import com.github.jond3k.jonstructs.blocks.{RetryBlock, SwallowBlock, TimeoutBlock}
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.slf4j.Logger
import java.io.IOException

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */

class RetryBlockTest
  extends FunSuite
  with TimeoutBlock
  with MustMatchers
  with MockitoSugar
  with RetryBlock {

  test("returns value on success") {
    retry(ms=0) { "cheese" } must equal("cheese")
  }

  test("the correct number of retries are attempted") {
    var i = 0
    val t = 2
    intercept[IllegalArgumentException] {
      retry(ms=0, retries=t) {
        i = i + 1
        throw new IllegalArgumentException("should be intercepted")
      }
    }
    i must equal(t+1)
  }

  test("the final fail reason bubbles up") {
    intercept[IllegalArgumentException] {
      var i = 0
      retry(ms=0, retries=2) {
        i = i + 1
        i match {
          case 3 => throw new IllegalArgumentException("should be intercepted")
          case _ => throw new NullPointerException("should not be intercepted")
        }
      }
    }
  }

  test("the retry logger is called with every failed attempt") {
    val t     = 2
    val log   = mock[Logger]
    val range = (0 to t).reverse
    val errs = for(i <- range) yield (i, new IOException("should be intercepted %s" format i))
    val iter = errs.iterator
    _retryLogger = log.error

    intercept[IOException] {
      retry(ms=0, retries=t) {
        throw iter.next()._2
      }
    }

    val riter = errs.take(1).iterator
    riter.foreach(i => verify(log).error(_retryString(i._1), i._2))
  }
}
