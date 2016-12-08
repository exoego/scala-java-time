package org.threeten.bp

import org.scalactic.{Prettifier, source}
import org.scalatest.Assertion
import org.scalatest.FunSuite

/**
  * Helper methods to avoid rewriting much of the TestNG tests
  */
trait AssertionsHelper { this: FunSuite =>
  def assertEquals[A, B](o1: A, o2: B, msg: String)(implicit prettifier: Prettifier, pos: source.Position): Assertion =
    assertEquals(o1, o2)

  def assertEquals[A, B](o1: A, o2: B)(implicit prettifier: Prettifier, pos: source.Position): Assertion =
    assert(o1 == o2)

  def assertNotEquals[A, B](o1: A, o2: B)(implicit prettifier: Prettifier, pos: source.Position): Assertion =
    assert(o1 != o2)

  def assertFalse(b: Boolean)(implicit prettifier: Prettifier, pos: source.Position): Assertion =
    assert(!b)

  def assertTrue(b: Boolean)(implicit prettifier: Prettifier, pos: source.Position): Assertion =
    assert(b)

  def assertNull[A](a: A)(implicit prettifier: Prettifier, pos: source.Position): Assertion =
    assert(a == null)

  def assertNotNull[A](a: A)(implicit prettifier: Prettifier, pos: source.Position): Assertion =
    assert(a != null)

}
