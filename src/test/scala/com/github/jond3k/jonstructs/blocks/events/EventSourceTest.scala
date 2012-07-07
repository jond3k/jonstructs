package com.github.jond3k.jonstructs.blocks.events

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.matchers.MustMatchers
import com.github.jond3k.jonstructs.events.{Observer, EventSource}
import org.scalatest.mock.MockitoSugar
import com.github.jond3k.jonstructs.blocks.DelayedBlock

class EventSourceTest
  extends FunSuite
  with MustMatchers
  with MockitoSugar {

  test("must have no observers by default") {
    val es = new EventSource[Int]()
    es.obs.size must equal(0)
  }

  test("Observers can be added to the list with the subscribe() method") {
      val es = new EventSource[Int]()
      val ob = new Observer[Int](es, x => x)
      es.subscribe(ob)
      es.obs must contain(ob)
  }

  test("Must allow multiple observers to subscribe") {
    val es = new EventSource[Int]()
    val ob1 = new Observer[Int](es, x => x)
    val ob2 = new Observer[Int](es, x => x)

    es.subscribe(ob1)
    es.subscribe(ob2)

    es.obs must contain(ob1)
    es.obs must contain(ob2)
  }

  test("Must not allow the same observer to be added twice") {
    val es = new EventSource[Int]()
    val ob = new Observer[Int](es, x => x)

    es.subscribe(ob)

    es.obs must contain(ob)
    es.obs.size must equal(1)
  }

  test("Must allow one observer to be unsubscribed") {
    val es = new EventSource[Int]
    val o = new Observer[Int](es, x => x)

    es.subscribe(o)
    es.unsubscribe(o)

    es.obs must not contain (o)
    es.obs.size must equal(0)
  }

  test("Must only remove the observer we have asked to unsubscribe") {
    val es = new EventSource[Int]()
    val ob1 = new Observer[Int](es, x => x)
    val ob2 = new Observer[Int](es, x => x)

    es.subscribe(ob1)
    es.subscribe(ob2)
    es.unsubscribe(ob1)

    es.obs must not contain (ob1)
    es.obs must contain(ob2)
  }

  test("Must fail silently if we attempt to unsubscribe an observer that is not already subscribed") {
    val es = new EventSource[Int]()
    val ob = new Observer[Int](es, x => x)
    es.unsubscribe(ob)
  }

}
