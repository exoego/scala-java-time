package org.threeten.bp

import java.util.{ Calendar, Locale, TimeZone }

import org.scalatest.FunSuite

class TestCalendar extends FunSuite with AssertionsHelper {

  private var defaultLocale: Locale = null

  private def newCal = {
    val instance = Calendar.getInstance(TimeZone.getTimeZone("EST"))
    instance.clear()
    instance
  }

  /* java.util.Calendar#set(int, int) */
  test("0: correct result defined by the last set field") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    assertEquals(cal.getTime.getTime, 1009861200000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    assertEquals(cal.getTime.getTime, 1014958800000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DATE, 24)
    assertEquals(cal.getTime.getTime, 1011848400000L)

    cal.set(Calendar.MONTH, Calendar.OCTOBER)
    cal.set(Calendar.DATE, 31)
    cal.set(Calendar.MONTH, Calendar.NOVEMBER)
    cal.set(Calendar.DATE, 26)
    assertEquals(cal.get(Calendar.MONTH), Calendar.NOVEMBER)

    val dow = cal.get(Calendar.DAY_OF_WEEK)
    cal.set(Calendar.DATE, 27)
    assertNotEquals(cal.get(Calendar.DAY_OF_WEEK), dow)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    assertEquals(cal.getTime.getTime, 1010379600000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
    assertEquals(cal.getTime.getTime, 1009861200000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
    assertEquals(cal.getTime.getTime, 1010034000000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_MONTH, 2)
    assertEquals(cal.getTime.getTime, 1010293200000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2)
    assertEquals(cal.getTime.getTime, 1010898000000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 11)
    assertEquals(cal.getTime.getTime, 1015736400000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DATE, 24)
    cal.set(Calendar.WEEK_OF_YEAR, 11)
    assertEquals(cal.getTime.getTime, 1011848400000L)

    cal.clear
    cal.set(Calendar.YEAR, 2002)
    cal.get(Calendar.WEEK_OF_YEAR) // Force fields to compute
    cal.set(Calendar.WEEK_OF_YEAR, 11)
    assertEquals(cal.getTime.getTime, 1015909200000L)
  }

  test("1: WEEK_OF_YEAR has priority over MONTH/DATE") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_YEAR, 170)
    cal.set(Calendar.WEEK_OF_YEAR, 11)
    cal.set(Calendar.MONTH, Calendar.JANUARY)
    cal.set(Calendar.DATE, 5)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("1a: WEEK_OF_YEAR has priority over MONTH/DATE") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 11)
    cal.set(Calendar.MONTH, Calendar.JANUARY)
    cal.set(Calendar.DATE, 5)
    cal.set(Calendar.DAY_OF_YEAR, 170)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("1b: DAY_OF_WEEK has no effect when other fields not set") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.DATE, 11)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("1c: Set DAY_OF_WEEK without DATE (Regression for HARMONY-4384)") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
    assertEquals(1015304400000L, cal.getTime.getTime)
  }

  test("2: WEEK_OF_MONTH has priority") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 12)
    cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1)
    cal.set(Calendar.WEEK_OF_MONTH, 3)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.DATE, 5)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("3:  DAY_OF_WEEK_IN_MONTH has priority over WEEK_OF_YEAR") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 12)
    cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.DATE, 5)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("4: WEEK_OF_MONTH has priority, MONTH not set") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 12)
    cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1)
    cal.set(Calendar.WEEK_OF_MONTH, 3)
    cal.set(Calendar.DATE, 25)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    assertEquals(cal.getTime.getTime, 1010984400000L)
  }

  test("5: WEEK_OF_YEAR has priority when MONTH set last and DAY_OF_WEEK set") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 11)
    cal.set(Calendar.DATE, 25)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.MONTH, Calendar.JANUARY)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("5a: Use MONTH/DATE when WEEK_OF_YEAR set but not DAY_OF_WEEK") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 12)
    cal.set(Calendar.DATE, 11)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("5b: Use MONTH/DATE when DAY_OF_WEEK is not set") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 12)
    cal.set(Calendar.DATE, 11)
    cal.set(Calendar.WEEK_OF_MONTH, 1)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("5c: WEEK_OF_MONTH has priority") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 12)
    cal.set(Calendar.DATE, 5)
    cal.set(Calendar.WEEK_OF_MONTH, 3)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("6: DATE has priority when set last") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 12)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.DATE, 11)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("7: DATE has priority when set last, MONTH not set") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 12)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.DATE, 14)
    assertEquals(cal.getTime.getTime, 1010984400000L)
  }

  test("8: DAY_OF_YEAR has priority when MONTH set last and DATE not set") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_YEAR, 70)
    cal.set(Calendar.MONTH, Calendar.JANUARY)
    assertTrue(cal.getTime.getTime == 1015822800000L)
  }

  test("8a: DAY/MONTH has priority when DATE set after DAY_OF_YEAR") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_YEAR, 170)
    cal.set(Calendar.DATE, 11)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("8b: DAY_OF_YEAR has priority when set after DATE") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DATE, 15)
    cal.set(Calendar.DAY_OF_YEAR, 70)
    cal.set(Calendar.MONTH, Calendar.JANUARY)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("9: DATE has priority when set last") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_YEAR, 70)
    cal.set(Calendar.DATE, 14)
    assertTrue(cal.getTime.getTime == 1010984400000L)
  }

  test("9a") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_YEAR, 15)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
    cal.set(Calendar.DATE, 14)
    assertEquals(cal.getTime.getTime, 1010984400000L)
  }

  test("9b") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.DATE, 14)
    cal.set(Calendar.WEEK_OF_YEAR, 11)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("9c") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DATE, 14)
    cal.set(Calendar.WEEK_OF_YEAR, 11)
    assertEquals(cal.getTime.getTime, 1010984400000L)
  }

  test("9d") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.WEEK_OF_MONTH, 1)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.DATE, 11)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("10: DAY_OF_YEAR has priority when DAY_OF_MONTH set last and other fields not set") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_YEAR, 70)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("11: MONTH/DATE has priority when DAY_OF_WEEK_IN_MONTH set last but DAY_OF_WEEK not set") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DATE, 11)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("12: MONTH/DATE has priority when WEEK_OF_YEAR set last but DAY_OF_WEEK") {
    val cal = newCal

    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DATE, 11)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.WEEK_OF_YEAR, 15)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("13: MONTH/DATE has priority when WEEK_OF_MONTH set last but DAY_OF_WEEK") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DATE, 11)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.WEEK_OF_MONTH, 1)
    assertEquals(cal.getTime.getTime, 1015822800000L)
  }

  test("14: Ensure last date field set is reset after computing") {
    val cal = newCal
    cal.set(Calendar.YEAR, 2002)
    cal.set(Calendar.DAY_OF_YEAR, 111)
    cal.get(Calendar.YEAR)
    cal.set(Calendar.MONTH, Calendar.MARCH)
    cal.set(Calendar.AM_PM, Calendar.AM)
    assertEquals(cal.getTime.getTime, 1016686800000L)
  }

  test("15: AM/PM") {
    val cal = newCal
    var hour = cal.get(Calendar.HOUR)
    cal.set(Calendar.HOUR, hour)
    cal.set(Calendar.AM_PM, Calendar.PM)
    assertEquals(Calendar.PM, cal.get(Calendar.AM_PM))
    // setting AM_PM without HOUR should not have any affect
    cal.set(Calendar.AM_PM, Calendar.AM)
    assertEquals(Calendar.AM, cal.get(Calendar.AM_PM))
    val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
    hour = cal.get(Calendar.HOUR)
    cal.set(Calendar.AM_PM, Calendar.PM)
    assertEquals(Calendar.PM, cal.get(Calendar.AM_PM))
    assertEquals(hour, cal.get(Calendar.HOUR))
    assertEquals(hourOfDay + 12, cal.get(Calendar.HOUR_OF_DAY))
  }

  test("15: regression test for Harmony-2122") {
    val cal = Calendar.getInstance
    val oldValue = cal.get(Calendar.AM_PM)
    var newValue = if (oldValue == Calendar.AM) Calendar.PM
    else Calendar.AM
    cal.set(Calendar.AM_PM, newValue)
    newValue = cal.get(Calendar.AM_PM)
    assertTrue(newValue != oldValue)
  }

}
