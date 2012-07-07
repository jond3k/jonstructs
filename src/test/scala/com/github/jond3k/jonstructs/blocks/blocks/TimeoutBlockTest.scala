package com.github.jond3k.jonstructs.blocks.blocks

import org.scalatest.FunSuite
import com.github.jond3k.jonstructs.blocks.{SwallowBlock, TimeoutBlock}
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar
import java.util.concurrent.TimeoutException

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */

class TimeoutBlockTest
  extends FunSuite
  with TimeoutBlock
  with MustMatchers
  with MockitoSugar
  with SwallowBlock {

  test("throws an exception if the task took too long") {
    intercept[TimeoutException] {
      timeout(ms=0) {
        Thread.sleep(1000)
      }
    }
  }

  test("returns the correct value if the task completed in time") {
    timeout(ms=1000) {
      "cheese"
    } must equal("cheese")
  }

  test("any underlying exception bubbles up to the top") {
    intercept[IllegalArgumentException] {
      timeout(ms=1000) {
        throw new IllegalArgumentException("should be caught")
      }
    }
  }
}
