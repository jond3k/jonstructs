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

  test("ensure timer fires at regular intervals (non-deterministic)") {
    var i = 0
    val timer = every(ms=10) {
      i = i + 1
    }
    Thread.sleep(200)
    timer.cancel()
    i must be >= 10
  }

}
