package com.github.jond3k.jonstructs.blocks.blocks

import org.scalatest.FunSuite
import com.github.jond3k.jonstructs.blocks.SwallowBlock
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.slf4j.Logger

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */

class SwallowBlockTest
  extends FunSuite
  with SwallowBlock
  with MustMatchers
  with MockitoSugar {

  def returns() = "data"
  def returnsZP = "data"

  def throws(): String = throw new RuntimeException("should be caught")
  def throwsZP: String = throw new RuntimeException("should be caught")

  test("returns some value if there was no exception") {
    swallow(returns) must equal(Some(returns()))
  }

  test("returns none if there was an exception") {
    swallow(throws) must equal(None)
  }

  test("returns some value if there was no exception (zero-paren)") {
    swallow(returnsZP) must equal(Some(returnsZP))
  }

  test("returns none if there was an exception (zero-paren)") {
    swallow(throwsZP) must equal(None)
  }

  test("swallows all exceptions without logging") {
    swallow(throw new RuntimeException("should be caught"))
  }

  test("swallows all exceptions and logs them") {
    val log = mock[Logger]
    val ex  = new RuntimeException("should be caught")
    swallow(log.error, throw ex) must equal(None)
    verify(log).error(_swallowString(ex))
  }
}
