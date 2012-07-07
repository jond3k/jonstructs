package com.github.jond3k.jonstructs.blocks.events

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar
import com.github.jond3k.jonstructs.events.{Observer, EventSource}
import org.mockito.Mockito._
import com.github.jond3k.jonstructs.blocks.ObserveBlock

class ObservingSpec
  extends FunSuite
  with MustMatchers
  with MockitoSugar {

  class ObserveBlockImpl extends ObserveBlock {
    override def observe[A](es: EventSource[A])(ev: A => Unit): Observer[A] = super.observe(es)(ev)
  }

  val notRaised = (x: Int) => fail("event should not be raised")

  test("Must support subscription to an EventSource") {
    val sut = new ObserveBlockImpl
    val es = mock[EventSource[Int]]
    val o: Observer[Int] = sut.observe(es)(notRaised)
    verify(es).subscribe(o)
  }

  test("Must support subscription to multiple EventSources") {
    val sut = new ObserveBlockImpl
    val ess = (0 to 3).map(i => mock[EventSource[Int]])
    val obs = ess.map(es => sut.observe(es)(notRaised))
    val itr = obs.iterator
    assert(ess.length>0 && obs.length>0)
    ess.foreach(es => verify(es).subscribe(itr.next()))
  }

  test("Must register a callback function with the associated EventSource") {
    val sut = new ObserveBlockImpl
    var triggered = 0
    val es = new EventSource[Int]
    sut.observe(es) {i => triggered = triggered + i }
    (0 to 3).foreach(i=>es.raise(1))
    triggered must equal(3)
  }
}
