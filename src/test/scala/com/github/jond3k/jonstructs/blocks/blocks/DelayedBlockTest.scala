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
    timer.cancel()
    Thread.sleep(200)
    b must equal(false)
  }

}
