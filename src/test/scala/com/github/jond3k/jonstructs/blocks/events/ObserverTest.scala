package com.github.jond3k.jonstructs.blocks.events

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar
import com.github.jond3k.jonstructs.events.{EventSource, Observer}

class ObserverTest
  extends FunSuite
  with MustMatchers
  with MockitoSugar {

  test("Must accept an EventSource and callback") {
    new Observer[Int](new EventSource[Int], x => x)
  }

  test("Must subscribe to the associated EventSource when initialized") {
    val es = new EventSource[Int]
    val o  = new Observer[Int](es, x => Unit)

    o.initialize()

    es.obs must contain(o)
    es.obs.size must equal(1)
  }

  test("Must remove the observer from the EventSource when disposed") {
    val es = new EventSource[Int]
    val o  = new Observer[Int](es, x => Unit)

    o.initialize()
    o.dispose()

    es.obs must not contain (o)
    es.obs.size must equal(0)
  }

  test("Must never have its callback triggered after dispose() has been called") {
    var actual = 0
    val es     = new EventSource[Int]
    val o      = new Observer[Int](es, x => actual = actual + x)

    o.initialize()
    es.raise(1)
    es.raise(1)
    o.dispose()
    es.raise(1)
    es.raise(1)

    actual must not equal (4)
    actual must equal(2)
  }
}
