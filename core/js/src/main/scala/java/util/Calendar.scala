/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.util

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.ObjectStreamField
import java.io.Serializable


/**
  * {@code Calendar} is an abstract base class for converting between a
  * {@code Date} object and a set of integer fields such as
  * {@code YEAR}, {@code MONTH}, {@code DAY}, * {@code HOUR}, and so on. (A {@code Date} object represents a
  * specific instant in time with millisecond precision. See {@link Date} for
  * information about the {@code Date} class.)
  *
  * <p>
  * Subclasses of {@code Calendar} interpret a {@code Date}
  * according to the rules of a specific calendar system.
  *
  * <p>
  * Like other locale-sensitive classes, {@code Calendar} provides a class
  * method, {@code getInstance}, for getting a default instance of
  * this class for general use. {@code Calendar}'s {@code getInstance} method
  * returns a calendar whose locale is based on system settings and whose time fields
  * have been initialized with the current date and time: <blockquote>
  *
  * <pre>Calendar rightNow = Calendar.getInstance()</pre>
  *
  * </blockquote>
  *
  * <p>
  * A {@code Calendar} object can produce all the time field values needed
  * to implement the date-time formatting for a particular language and calendar
  * style (for example, Japanese-Gregorian, Japanese-Traditional).
  * {@code Calendar} defines the range of values returned by certain
  * fields, as well as their meaning. For example, the first month of the year
  * has value {@code MONTH} == {@code JANUARY} for all calendars.
  * Other values are defined by the concrete subclass, such as {@code ERA}
  * and {@code YEAR}. See individual field documentation and subclass
  * documentation for details.
  *
  * <p>
  * When a {@code Calendar} is <em>lenient</em>, it accepts a wider
  * range of field values than it produces. For example, a lenient
  * {@code GregorianCalendar} interprets {@code MONTH} ==
  * {@code JANUARY}, {@code DAY_OF_MONTH} == 32 as February 1. A
  * non-lenient {@code GregorianCalendar} throws an exception when given
  * out-of-range field settings. When calendars recompute field values for return
  * by {@code get()}, they normalize them. For example, a
  * {@code GregorianCalendar} always produces {@code DAY_OF_MONTH}
  * values between 1 and the length of the month.
  *
  * <p>
  * {@code Calendar} defines a locale-specific seven day week using two
  * parameters: the first day of the week and the minimal days in first week
  * (from 1 to 7). These numbers are taken from the locale resource data when a
  * {@code Calendar} is constructed. They may also be specified explicitly
  * through the API.
  *
  * <p>
  * When setting or getting the {@code WEEK_OF_MONTH} or
  * {@code WEEK_OF_YEAR} fields, {@code Calendar} must determine
  * the first week of the month or year as a reference point. The first week of a
  * month or year is defined as the earliest seven day period beginning on
  * {@code getFirstDayOfWeek()} and containing at least
  * {@code getMinimalDaysInFirstWeek()} days of that month or year. Weeks
  * numbered ..., -1, 0 precede the first week; weeks numbered 2, 3,... follow
  * it. Note that the normalized numbering returned by {@code get()} may
  * be different. For example, a specific {@code Calendar} subclass may
  * designate the week before week 1 of a year as week <em>n</em> of the
  * previous year.
  *
  * <p>
  * When computing a {@code Date} from time fields, two special
  * circumstances may arise: there may be insufficient information to compute the
  * {@code Date} (such as only year and month but no day in the month), or
  * there may be inconsistent information (such as "Tuesday, July 15, 1996" --
  * July 15, 1996 is actually a Monday).
  *
  * <p>
  * <strong>Insufficient information.</strong> The calendar will use default
  * information to specify the missing fields. This may vary by calendar; for the
  * Gregorian calendar, the default for a field is the same as that of the start
  * of the epoch: i.e., YEAR = 1970, MONTH = JANUARY, DATE = 1, etc.
  *
  * <p>
  * <strong>Inconsistent information.</strong> If fields conflict, the calendar
  * will give preference to fields set more recently. For example, when
  * determining the day, the calendar will look for one of the following
  * combinations of fields. The most recent combination, as determined by the
  * most recently set single field, will be used.
  *
  * <blockquote>
  *
  * <pre>
  * MONTH + DAY_OF_MONTH
  * MONTH + WEEK_OF_MONTH + DAY_OF_WEEK
  * MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
  * DAY_OF_YEAR
  * DAY_OF_WEEK + WEEK_OF_YEAR</pre>
  *
  * </blockquote>
  *
  * For the time of day:
  *
  * <blockquote>
  *
  * <pre>
  * HOUR_OF_DAY
  * AM_PM + HOUR</pre>
  *
  * </blockquote>
  *
  * <p>
  * <strong>Note:</strong> There are certain possible ambiguities in
  * interpretation of certain singular times, which are resolved in the following
  * ways:
  * <ol>
  * <li> 24:00:00 "belongs" to the following day. That is, 23:59 on Dec 31, 1969
  * &lt; 24:00 on Jan 1, 1970 &lt; 24:01:00 on Jan 1, 1970 form a sequence of
  * three consecutive minutes in time.
  *
  * <li> Although historically not precise, midnight also belongs to "am", and
  * noon belongs to "pm", so on the same day, we have 12:00 am (midnight) &lt; 12:01 am, * and 12:00 pm (noon) &lt; 12:01 pm
  * </ol>
  *
  * <p>
  * The date or time format strings are not part of the definition of a calendar, * as those must be modifiable or overridable by the user at runtime. Use
  * {@link java.text.DateFormat} to format dates.
  *
  * <p>
  * <strong>Field manipulation methods</strong>
  *
  * <p>
  * {@code Calendar} fields can be changed using three methods:
  * {@code set()}, {@code add()}, and {@code roll()}.
  *
  * <p>
  * <strong>{@code set(f, value)}</strong> changes field {@code f}
  * to {@code value}. In addition, it sets an internal member variable to
  * indicate that field {@code f} has been changed. Although field
  * {@code f} is changed immediately, the calendar's milliseconds is not
  * recomputed until the next call to {@code get()}, * {@code getTime()}, or {@code getTimeInMillis()} is made. Thus, * multiple calls to {@code set()} do not trigger multiple, unnecessary
  * computations. As a result of changing a field using {@code set()}, * other fields may also change, depending on the field, the field value, and
  * the calendar system. In addition, {@code get(f)} will not necessarily
  * return {@code value} after the fields have been recomputed. The
  * specifics are determined by the concrete calendar class.
  *
  * <p>
  * <em>Example</em>: Consider a {@code GregorianCalendar} originally
  * set to August 31, 1999. Calling <code>set(Calendar.MONTH, * Calendar.SEPTEMBER)</code>
  * sets the calendar to September 31, 1999. This is a temporary internal
  * representation that resolves to October 1, 1999 if {@code getTime()}is
  * then called. However, a call to {@code set(Calendar.DAY_OF_MONTH, 30)}
  * before the call to {@code getTime()} sets the calendar to September
  * 30, 1999, since no recomputation occurs after {@code set()} itself.
  *
  * <p>
  * <strong>{@code add(f, delta)}</strong> adds {@code delta} to
  * field {@code f}. This is equivalent to calling <code>set(f, * get(f) + delta)</code>
  * with two adjustments:
  *
  * <blockquote>
  * <p>
  * <strong>Add rule 1</strong>. The value of field {@code f} after the
  * call minus the value of field {@code f} before the call is
  * {@code delta}, modulo any overflow that has occurred in field
  * {@code f}. Overflow occurs when a field value exceeds its range and, * as a result, the next larger field is incremented or decremented and the
  * field value is adjusted back into its range.
  *
  * <p>
  * <strong>Add rule 2</strong>. If a smaller field is expected to be invariant, * but &nbsp; it is impossible for it to be equal to its prior value because of
  * changes in its minimum or maximum after field {@code f} is changed, * then its value is adjusted to be as close as possible to its expected value.
  * A smaller field represents a smaller unit of time. {@code HOUR} is a
  * smaller field than {@code DAY_OF_MONTH}. No adjustment is made to
  * smaller fields that are not expected to be invariant. The calendar system
  * determines what fields are expected to be invariant.
  * </blockquote>
  *
  * <p>
  * In addition, unlike {@code set()}, {@code add()} forces an
  * immediate recomputation of the calendar's milliseconds and all fields.
  *
  * <p>
  * <em>Example</em>: Consider a {@code GregorianCalendar} originally
  * set to August 31, 1999. Calling {@code add(Calendar.MONTH, 13)} sets
  * the calendar to September 30, 2000. <strong>Add rule 1</strong> sets the
  * {@code MONTH} field to September, since adding 13 months to August
  * gives September of the next year. Since {@code DAY_OF_MONTH} cannot be
  * 31 in September in a {@code GregorianCalendar}, <strong>add rule 2</strong>
  * sets the {@code DAY_OF_MONTH} to 30, the closest possible value.
  * Although it is a smaller field, {@code DAY_OF_WEEK} is not adjusted by
  * rule 2, since it is expected to change when the month changes in a
  * {@code GregorianCalendar}.
  *
  * <p>
  * <strong>{@code roll(f, delta)}</strong> adds {@code delta} to
  * field {@code f} without changing larger fields. This is equivalent to
  * calling {@code add(f, delta)} with the following adjustment:
  *
  * <blockquote>
  * <p>
  * <strong>Roll rule</strong>. Larger fields are unchanged after the call. A
  * larger field represents a larger unit of time. {@code DAY_OF_MONTH} is
  * a larger field than {@code HOUR}.
  * </blockquote>
  *
  * <p>
  * <em>Example</em>: Consider a {@code GregorianCalendar} originally
  * set to August 31, 1999. Calling <code>roll(Calendar.MONTH, * 8)</code> sets
  * the calendar to April 30, <strong>1999</strong>. Add rule 1 sets the
  * {@code MONTH} field to April. Using a {@code GregorianCalendar}, * the {@code DAY_OF_MONTH} cannot be 31 in the month April. Add rule 2
  * sets it to the closest possible value, 30. Finally, the <strong>roll rule</strong>
  * maintains the {@code YEAR} field value of 1999.
  *
  * <p>
  * <em>Example</em>: Consider a {@code GregorianCalendar} originally
  * set to Sunday June 6, 1999. Calling
  * {@code roll(Calendar.WEEK_OF_MONTH, -1)} sets the calendar to Tuesday
  * June 1, 1999, whereas calling {@code add(Calendar.WEEK_OF_MONTH, -1)}
  * sets the calendar to Sunday May 30, 1999. This is because the roll rule
  * imposes an additional constraint: The {@code MONTH} must not change
  * when the {@code WEEK_OF_MONTH} is rolled. Taken together with add rule
  * 1, the resultant date must be between Tuesday June 1 and Saturday June 5.
  * According to add rule 2, the {@code DAY_OF_WEEK}, an invariant when
  * changing the {@code WEEK_OF_MONTH}, is set to Tuesday, the closest
  * possible value to Sunday (where Sunday is the first day of the week).
  *
  * <p>
  * <strong>Usage model</strong>. To motivate the behavior of {@code add()}
  * and {@code roll()}, consider a user interface component with
  * increment and decrement buttons for the month, day, and year, and an
  * underlying {@code GregorianCalendar}. If the interface reads January
  * 31, 1999 and the user presses the month increment button, what should it
  * read? If the underlying implementation uses {@code set()}, it might
  * read March 3, 1999. A better result would be February 28, 1999. Furthermore, * if the user presses the month increment button again, it should read March
  * 31, 1999, not March 28, 1999. By saving the original date and using either
  * {@code add()} or {@code roll()}, depending on whether larger
  * fields should be affected, the user interface can behave as most users will
  * intuitively expect.
  *
  * <p>
  * <b>Note:</b> You should always use {@code roll} and {@code add} rather than
  * attempting to perform arithmetic operations directly on the fields of a
  * <tt>Calendar</tt>. It is quite possible for <tt>Calendar</tt> subclasses
  * to have fields with non-linear behavior, for example missing months or days
  * during non-leap years. The subclasses' <tt>add</tt> and <tt>roll</tt>
  * methods will take this into account, while simple arithmetic manipulations
  * may give invalid results.
  *
  * @see Date
  * @see GregorianCalendar
  * @see TimeZone
  */
@SerialVersionUID(-1807547505821590642L)
object Calendar {
  /**
    * Value of the {@code MONTH} field indicating the first month of the
    * year.
    */
  val JANUARY = 0
  /**
    * Value of the {@code MONTH} field indicating the second month of
    * the year.
    */
  val FEBRUARY = 1
  /**
    * Value of the {@code MONTH} field indicating the third month of the
    * year.
    */
  val MARCH = 2
  /**
    * Value of the {@code MONTH} field indicating the fourth month of
    * the year.
    */
  val APRIL = 3
  /**
    * Value of the {@code MONTH} field indicating the fifth month of the
    * year.
    */
  val MAY = 4
  /**
    * Value of the {@code MONTH} field indicating the sixth month of the
    * year.
    */
  val JUNE = 5
  /**
    * Value of the {@code MONTH} field indicating the seventh month of
    * the year.
    */
  val JULY = 6
  /**
    * Value of the {@code MONTH} field indicating the eighth month of
    * the year.
    */
  val AUGUST = 7
  /**
    * Value of the {@code MONTH} field indicating the ninth month of the
    * year.
    */
  val SEPTEMBER = 8
  /**
    * Value of the {@code MONTH} field indicating the tenth month of the
    * year.
    */
  val OCTOBER = 9
  /**
    * Value of the {@code MONTH} field indicating the eleventh month of
    * the year.
    */
  val NOVEMBER = 10
  /**
    * Value of the {@code MONTH} field indicating the twelfth month of
    * the year.
    */
  val DECEMBER = 11
  /**
    * Value of the {@code MONTH} field indicating the thirteenth month
    * of the year. Although {@code GregorianCalendar} does not use this
    * value, lunar calendars do.
    */
  val UNDECIMBER = 12
  /**
    * Value of the {@code DAY_OF_WEEK} field indicating Sunday.
    */
  val SUNDAY = 1
  /**
    * Value of the {@code DAY_OF_WEEK} field indicating Monday.
    */
  val MONDAY = 2
  /**
    * Value of the {@code DAY_OF_WEEK} field indicating Tuesday.
    */
  val TUESDAY = 3
  /**
    * Value of the {@code DAY_OF_WEEK} field indicating Wednesday.
    */
  val WEDNESDAY = 4
  /**
    * Value of the {@code DAY_OF_WEEK} field indicating Thursday.
    */
  val THURSDAY = 5
  /**
    * Value of the {@code DAY_OF_WEEK} field indicating Friday.
    */
  val FRIDAY = 6
  /**
    * Value of the {@code DAY_OF_WEEK} field indicating Saturday.
    */
  val SATURDAY = 7
  /**
    * Field number for {@code get} and {@code set} indicating the
    * era, e.g., AD or BC in the Julian calendar. This is a calendar-specific
    * value; see subclass documentation.
    *
    * @see GregorianCalendar#AD
    * @see GregorianCalendar#BC
    */
  val ERA = 0
  /**
    * Field number for {@code get} and {@code set} indicating the
    * year. This is a calendar-specific value; see subclass documentation.
    */
  val YEAR = 1
  /**
    * Field number for {@code get} and {@code set} indicating the
    * month. This is a calendar-specific value. The first month of the year is
    * {@code JANUARY}; the last depends on the number of months in a
    * year.
    *
    * @see #JANUARY
    * @see #FEBRUARY
    * @see #MARCH
    * @see #APRIL
    * @see #MAY
    * @see #JUNE
    * @see #JULY
    * @see #AUGUST
    * @see #SEPTEMBER
    * @see #OCTOBER
    * @see #NOVEMBER
    * @see #DECEMBER
    * @see #UNDECIMBER
    */
  val MONTH = 2
  /**
    * Field number for {@code get} and {@code set} indicating the
    * week number within the current year. The first week of the year, as
    * defined by {@code getFirstDayOfWeek()} and
    * {@code getMinimalDaysInFirstWeek()}, has value 1. Subclasses
    * define the value of {@code WEEK_OF_YEAR} for days before the first
    * week of the year.
    *
    * @see #getFirstDayOfWeek
    * @see #getMinimalDaysInFirstWeek
    */
  val WEEK_OF_YEAR = 3
  /**
    * Field number for {@code get} and {@code set} indicating the
    * week number within the current month. The first week of the month, as
    * defined by {@code getFirstDayOfWeek()} and
    * {@code getMinimalDaysInFirstWeek()}, has value 1. Subclasses
    * define the value of {@code WEEK_OF_MONTH} for days before the
    * first week of the month.
    *
    * @see #getFirstDayOfWeek
    * @see #getMinimalDaysInFirstWeek
    */
  val WEEK_OF_MONTH = 4
  /**
    * Field number for {@code get} and {@code set} indicating the
    * day of the month. This is a synonym for {@code DAY_OF_MONTH}. The
    * first day of the month has value 1.
    *
    * @see #DAY_OF_MONTH
    */
  val DATE = 5
  /**
    * Field number for {@code get} and {@code set} indicating the
    * day of the month. This is a synonym for {@code DATE}. The first
    * day of the month has value 1.
    *
    * @see #DATE
    */
  val DAY_OF_MONTH = 5
  /**
    * Field number for {@code get} and {@code set} indicating the
    * day number within the current year. The first day of the year has value
    * 1.
    */
  val DAY_OF_YEAR = 6
  /**
    * Field number for {@code get} and {@code set} indicating the
    * day of the week. This field takes values {@code SUNDAY},     * {@code MONDAY}, {@code TUESDAY}, {@code WEDNESDAY},     * {@code THURSDAY}, {@code FRIDAY}, and
    * {@code SATURDAY}.
    *
    * @see #SUNDAY
    * @see #MONDAY
    * @see #TUESDAY
    * @see #WEDNESDAY
    * @see #THURSDAY
    * @see #FRIDAY
    * @see #SATURDAY
    */
  val DAY_OF_WEEK = 7
  /**
    * Field number for {@code get} and {@code set} indicating the
    * ordinal number of the day of the week within the current month. Together
    * with the {@code DAY_OF_WEEK} field, this uniquely specifies a day
    * within a month. Unlike {@code WEEK_OF_MONTH} and
    * {@code WEEK_OF_YEAR}, this field's value does <em>not</em>
    * depend on {@code getFirstDayOfWeek()} or
    * {@code getMinimalDaysInFirstWeek()}. {@code DAY_OF_MONTH 1}
    * through {@code 7} always correspond to <code>DAY_OF_WEEK_IN_MONTH
    * 1</code>;
    * {@code 8} through {@code 15} correspond to
    * {@code DAY_OF_WEEK_IN_MONTH 2}, and so on.
    * {@code DAY_OF_WEEK_IN_MONTH 0} indicates the week before
    * {@code DAY_OF_WEEK_IN_MONTH 1}. Negative values count back from
    * the end of the month, so the last Sunday of a month is specified as
    * {@code DAY_OF_WEEK = SUNDAY, DAY_OF_WEEK_IN_MONTH = -1}. Because
    * negative values count backward they will usually be aligned differently
    * within the month than positive values. For example, if a month has 31
    * days, {@code DAY_OF_WEEK_IN_MONTH -1} will overlap
    * {@code DAY_OF_WEEK_IN_MONTH 5} and the end of {@code 4}.
    *
    * @see #DAY_OF_WEEK
    * @see #WEEK_OF_MONTH
    */
  val DAY_OF_WEEK_IN_MONTH = 8
  /**
    * Field number for {@code get} and {@code set} indicating
    * whether the {@code HOUR} is before or after noon. E.g., at
    * 10:04:15.250 PM the {@code AM_PM} is {@code PM}.
    *
    * @see #AM
    * @see #PM
    * @see #HOUR
    */
  val AM_PM = 9
  /**
    * Field number for {@code get} and {@code set} indicating the
    * hour of the morning or afternoon. {@code HOUR} is used for the
    * 12-hour clock. E.g., at 10:04:15.250 PM the {@code HOUR} is 10.
    *
    * @see #AM_PM
    * @see #HOUR_OF_DAY
    */
  val HOUR = 10
  /**
    * Field number for {@code get} and {@code set} indicating the
    * hour of the day. {@code HOUR_OF_DAY} is used for the 24-hour
    * clock. E.g., at 10:04:15.250 PM the {@code HOUR_OF_DAY} is 22.
    *
    * @see #HOUR
    */
  val HOUR_OF_DAY = 11
  /**
    * Field number for {@code get} and {@code set} indicating the
    * minute within the hour. E.g., at 10:04:15.250 PM the {@code MINUTE}
    * is 4.
    */
  val MINUTE = 12
  /**
    * Field number for {@code get} and {@code set} indicating the
    * second within the minute. E.g., at 10:04:15.250 PM the
    * {@code SECOND} is 15.
    */
  val SECOND = 13
  /**
    * Field number for {@code get} and {@code set} indicating the
    * millisecond within the second. E.g., at 10:04:15.250 PM the
    * {@code MILLISECOND} is 250.
    */
  val MILLISECOND = 14
  /**
    * Field number for {@code get} and {@code set} indicating the
    * raw offset from GMT in milliseconds.
    */
  val ZONE_OFFSET = 15
  /**
    * Field number for {@code get} and {@code set} indicating the
    * daylight savings offset in milliseconds.
    */
  val DST_OFFSET = 16
  /**
    * This is the total number of fields in this calendar.
    */
  val FIELD_COUNT = 17
  /**
    * Value of the {@code AM_PM} field indicating the period of the day
    * from midnight to just before noon.
    */
  val AM = 0
  /**
    * Value of the {@code AM_PM} field indicating the period of the day
    * from noon to just before midnight.
    */
  val PM = 1

  private val fieldNames = Array("ERA=", "YEAR=", "MONTH=", "WEEK_OF_YEAR=", "WEEK_OF_MONTH=", "DAY_OF_MONTH=", "DAY_OF_YEAR=", "DAY_OF_WEEK=", "DAY_OF_WEEK_IN_MONTH=", "AM_PM=", "HOUR=", "HOUR_OF_DAY", "MINUTE=", "SECOND=", "MILLISECOND=", "ZONE_OFFSET=", "DST_OFFSET=")

  /**
    * Gets the list of installed {@code Locale}s which support {@code Calendar}.
    *
    * @return an array of { @code Locale}.
    */
  def getAvailableLocales: Array[Locale] = Locale.getAvailableLocales

  /**
    * Constructs a new instance of the {@code Calendar} subclass appropriate for the
    * default {@code Locale}.
    *
    * @return a { @code Calendar} subclass instance set to the current date and time in
    *                   the default { @code Timezone}.
    */
  def getInstance: Calendar = new GregorianCalendar()

  /**
    * Constructs a new instance of the {@code Calendar} subclass appropriate for the
    * specified {@code Locale}.
    *
    * @param locale
    * the locale to use.
    * @return a { @code Calendar} subclass instance set to the current date and time.
    */
  def getInstance(locale: Locale): Calendar = new GregorianCalendar(locale)

  /**
    * Constructs a new instance of the {@code Calendar} subclass appropriate for the
    * default {@code Locale}, using the specified {@code TimeZone}.
    *
    * @param timezone
    * the { @code TimeZone} to use.
    * @return a { @code Calendar} subclass instance set to the current date and time in
    *                   the specified timezone.
    */
  def getInstance(timezone: TimeZone): Calendar = new GregorianCalendar(timezone)

  /**
    * Constructs a new instance of the {@code Calendar} subclass appropriate for the
    * specified {@code Locale}.
    *
    * @param timezone
    * the { @code TimeZone} to use.
    * @param locale
    * the { @code Locale} to use.
    * @return a { @code Calendar} subclass instance set to the current date and time in
    *                   the specified timezone.
    */
  def getInstance(timezone: TimeZone, locale: Locale) = new GregorianCalendar(timezone, locale)
}

@SerialVersionUID(-1807547505821590642L)
abstract class Calendar private[util](timezone: TimeZone, locale: Locale) extends Comparable[Calendar] {
  def this() {
    this(TimeZone.getDefault, Locale.getDefault)
  }
  def this(timezone: TimeZone) {
    this(timezone, Locale.getDefault)
  }
  def this(locale: Locale) {
    this(TimeZone.getDefault, locale)
  }

  setFirstDayOfWeek(0)
  setMinimalDaysInFirstWeek(0)
  setLenient(true)
  setTimeZone(timezone)

  protected var areFieldsSet: Boolean = _
  protected var fields: Array[Int] = new Array[Int](Calendar.FIELD_COUNT)
  /**
    * A boolean array. Each element indicates if the corresponding field has
    * been set. The length is {@code FIELD_COUNT}.
    */
  protected var isSet: Array[Boolean] = new Array[Boolean](Calendar.FIELD_COUNT)
  /**
    * Set to {@code true} when the time has been set, set to {@code false} when a field is
    * changed and the time must be recomputed.
    */
  protected var isTimeSet: Boolean = _
  /**
    * The time in milliseconds since January 1, 1970.
    */
  protected var time: Long = _
  private[util] var lastTimeFieldSet: Int = _
  private[util] var lastDateFieldSet: Int = _
  private var lenient: Boolean = _
  private var firstDayOfWeek: Int = _
  private var minimalDaysInFirstWeek: Int = _
  private var zone: TimeZone = _


  /**
    * Adds the specified amount to a {@code Calendar} field.
    *
    * @param field
    * the { @code Calendar} field to modify.
    * @param value
    * the amount to add to the field.
    * @throws IllegalArgumentException
    * if { @code field} is { @code DST_OFFSET} or { @code
    *            ZONE_OFFSET}.
    */
  def add(field: Int, value: Int): Unit

  /**
    * Returns whether the {@code Date} specified by this {@code Calendar} instance is after the {@code Date}
    * specified by the parameter. The comparison is not dependent on the time
    * zones of the {@code Calendar}.
    *
    * @param calendar
    * the { @code Calendar} instance to compare.
    * @return { @code true} when this Calendar is after calendar, { @code false} otherwise.
    * @throws IllegalArgumentException
    * if the time is not set and the time cannot be computed
    * from the current field values.
    */
  def after(calendar: AnyRef): Boolean = {
    if (!calendar.isInstanceOf[Calendar]) return false
    getTimeInMillis > calendar.asInstanceOf[Calendar].getTimeInMillis
  }

  /**
    * Returns whether the {@code Date} specified by this {@code Calendar} instance is before the
    * {@code Date} specified by the parameter. The comparison is not dependent on the
    * time zones of the {@code Calendar}.
    *
    * @param calendar
    * the { @code Calendar} instance to compare.
    * @return { @code true} when this Calendar is before calendar, { @code false} otherwise.
    * @throws IllegalArgumentException
    * if the time is not set and the time cannot be computed
    * from the current field values.
    */
  def before(calendar: AnyRef): Boolean = {
    if (!calendar.isInstanceOf[Calendar]) return false
    getTimeInMillis < calendar.asInstanceOf[Calendar].getTimeInMillis
  }

  /**
    * Clears all of the fields of this {@code Calendar}. All fields are initialized to
    * zero.
    */
  final def clear(): Unit = {
    var i = 0
    while (i < Calendar.FIELD_COUNT) {
      fields(i) = 0
      isSet.update(i, false)
      i += 1
    }
    areFieldsSet = false
    isTimeSet = false
  }

  /**
    * Clears the specified field to zero and sets the isSet flag to {@code false}.
    *
    * @param field
    * the field to clear.
    */
  final def clear(field: Int): Unit = {
    fields(field) = 0
    isSet.update(field, false)
    areFieldsSet = false
    isTimeSet = false
  }

  /**
    * Computes the time from the fields if the time has not already been set.
    * Computes the fields from the time if the fields are not already set.
    *
    * @throws IllegalArgumentException
    * if the time is not set and the time cannot be computed
    * from the current field values.
    */
  protected def complete(): Unit = {
    if (!isTimeSet) {
      computeTime()
      isTimeSet = true
    }
    if (!areFieldsSet) {
      computeFields()
      areFieldsSet = true
    }
  }

  /**
    * Computes the {@code Calendar} fields from {@code time}.
    */
  protected def computeFields(): Unit

  /**
    * Computes {@code time} from the Calendar fields.
    *
    * @throws IllegalArgumentException
    * if the time cannot be computed from the current field
    * values.
    */
  protected def computeTime(): Unit

  override def equals(other: Any): Boolean = {
    if (!other.isInstanceOf[Calendar]) return false
    val cal = other.asInstanceOf[Calendar]
    (this eq cal) || getTimeInMillis == cal.getTimeInMillis && isLenient == cal.isLenient && getFirstDayOfWeek == cal.getFirstDayOfWeek && getMinimalDaysInFirstWeek == cal.getMinimalDaysInFirstWeek && getTimeZone.equals(cal.getTimeZone)
  }

  /**
    * Gets the value of the specified field after computing the field values by
    * calling {@code complete()} first.
    *
    * @param field
    * the field to get.
    * @return the value of the specified field.
    * @throws IllegalArgumentException
    * if the fields are not set, the time is not set, and the
    * time cannot be computed from the current field values.
    * @throws ArrayIndexOutOfBoundsException
    * if the field is not inside the range of possible fields.
    * The range is starting at 0 up to { @code FIELD_COUNT}.
    */
  def get(field: Int): Int = {
    complete()
    fields(field)
  }

  /**
    * Gets the maximum value of the specified field for the current date.
    *
    * @param field
    * the field.
    * @return the maximum value of the specified field.
    */
  def getActualMaximum(field: Int): Int = {
    var value = 0
    var next = getLeastMaximum(field)
    if (getMaximum(field) == next) return next
    complete()
    val orgTime = time
    set(field, next)
    do {
      value = next
      roll(field, true)
      next = get(field)
    } while ( {
      next > value
    })
    time = orgTime
    areFieldsSet = false
    value
  }

  /**
    * Gets the minimum value of the specified field for the current date.
    *
    * @param field
    * the field.
    * @return the minimum value of the specified field.
    */
  def getActualMinimum(field: Int): Int = {
    var value = 0
    var next = getGreatestMinimum(field)
    if (getMinimum(field) == next) return next
    complete()
    val orgTime = time
    set(field, next)
    do {
      value = next
      roll(field, false)
      next = get(field)
    } while ( {
      next < value
    })
    time = orgTime
    areFieldsSet = false
    value
  }

  /**
    * Gets the first day of the week for this {@code Calendar}.
    *
    * @return the first day of the week.
    */
  def getFirstDayOfWeek: Int = firstDayOfWeek

  /**
    * Gets the greatest minimum value of the specified field. This is the
    * biggest value that {@code getActualMinimum} can return for any possible
    * time.
    *
    * @param field
    * the field.
    * @return the greatest minimum value of the specified field.
    */
  def getGreatestMinimum(field: Int): Int

  /**
    * Gets the smallest maximum value of the specified field. This is the
    * smallest value that {@code getActualMaximum()} can return for any
    * possible time.
    *
    * @param field
    * the field number.
    * @return the smallest maximum value of the specified field.
    */
  def getLeastMaximum(field: Int): Int

  /**
    * Gets the greatest maximum value of the specified field. This returns the
    * biggest value that {@code get} can return for the specified field.
    *
    * @param field
    * the field.
    * @return the greatest maximum value of the specified field.
    */
  def getMaximum(field: Int): Int

  /**
    * Gets the minimal days in the first week of the year.
    *
    * @return the minimal days in the first week of the year.
    */
  def getMinimalDaysInFirstWeek: Int = minimalDaysInFirstWeek

  /**
    * Gets the smallest minimum value of the specified field. this returns the
    * smallest value thet {@code get} can return for the specified field.
    *
    * @param field
    * the field number.
    * @return the smallest minimum value of the specified field.
    */
  def getMinimum(field: Int): Int

  /**
    * Gets the time of this {@code Calendar} as a {@code Date} object.
    *
    * @return a new { @code Date} initialized to the time of this { @code Calendar}.
    * @throws IllegalArgumentException
    * if the time is not set and the time cannot be computed
    * from the current field values.
    */
  final def getTime = new Date(getTimeInMillis)

  /**
    * Computes the time from the fields if required and returns the time.
    *
    * @return the time of this { @code Calendar}.
    * @throws IllegalArgumentException
    * if the time is not set and the time cannot be computed
    * from the current field values.
    */
  def getTimeInMillis: Long = {
    if (!isTimeSet) {
      computeTime()
      isTimeSet = true
    }
    time
  }

  /**
    * Gets the timezone of this {@code Calendar}.
    *
    * @return the { @code TimeZone} used by this { @code Calendar}.
    */
  def getTimeZone: TimeZone = zone

  /**
    * Returns an integer hash code for the receiver. Objects which are equal
    * return the same value for this method.
    *
    * @return the receiver's hash.
    * @see #equals
    */
  override def hashCode: Int = (if (isLenient) 1237
  else 1231) + getFirstDayOfWeek + getMinimalDaysInFirstWeek + getTimeZone.hashCode

  /**
    * Gets the value of the specified field without recomputing.
    *
    * @param field
    * the field.
    * @return the value of the specified field.
    */
  final protected def internalGet(field: Int): Int = fields(field)

  /**
    * Returns if this {@code Calendar} accepts field values which are outside the valid
    * range for the field.
    *
    * @return { @code true} if this { @code Calendar} is lenient, { @code false} otherwise.
    */
  def isLenient: Boolean = lenient

  /**
    * Returns whether the specified field is set.
    *
    * @param field
    * a { @code Calendar} field number.
    * @return { @code true} if the specified field is set, { @code false} otherwise.
    */
  final def isSet(field: Int): Boolean = isSet.apply(field)

  /**
    * Adds the specified amount to the specified field and wraps the value of
    * the field when it goes beyond the maximum or minimum value for the
    * current date. Other fields will be adjusted as required to maintain a
    * consistent date.
    *
    * @param field
    * the field to roll.
    * @param value
    * the amount to add.
    */
  def roll(field: Int, value: Int): Unit = {
    val increment = value >= 0
    val count = if (increment) value
    else -value
    var i = 0
    while ( {
      i < count
    }) {
      roll(field, increment)

      {
        i += 1; i - 1
      }
    }
  }

  /**
    * Increment or decrement the specified field and wrap the value of the
    * field when it goes beyond the maximum or minimum value for the current
    * date. Other fields will be adjusted as required to maintain a consistent
    * date.
    *
    * @param field
    * the number indicating the field to roll.
    * @param increment
    * { @code true} to increment the field, { @code false} to decrement.
    */
  def roll(field: Int, increment: Boolean): Unit

  /**
    * Sets a field to the specified value.
    *
    * @param field
    * the code indicating the { @code Calendar} field to modify.
    * @param value
    * the value.
    */
  def set(field: Int, value: Int): Unit = {
    fields(field) = value
    isSet.update(field, true)
    areFieldsSet = false
    isTimeSet = false
    if (field > Calendar.MONTH && field < Calendar.AM_PM) lastDateFieldSet = field
    if (field == Calendar.HOUR || field == Calendar.HOUR_OF_DAY) lastTimeFieldSet = field
    if (field == Calendar.AM_PM) lastTimeFieldSet = Calendar.HOUR
  }

  /**
    * Sets the year, month and day of the month fields. Other fields are not
    * changed.
    *
    * @param year
    * the year.
    * @param month
    * the month.
    * @param day
    * the day of the month.
    */
  final def set(year: Int, month: Int, day: Int): Unit = {
    set(Calendar.YEAR, year)
    set(Calendar.MONTH, month)
    set(Calendar.DATE, day)
  }

  /**
    * Sets the year, month, day of the month, hour of day and minute fields.
    * Other fields are not changed.
    *
    * @param year
    * the year.
    * @param month
    * the month.
    * @param day
    * the day of the month.
    * @param hourOfDay
    * the hour of day.
    * @param minute
    * the minute.
    */
  final def set(year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int): Unit = {
    set(year, month, day)
    set(Calendar.HOUR_OF_DAY, hourOfDay)
    set(Calendar.MINUTE, minute)
  }

  /**
    * Sets the year, month, day of the month, hour of day, minute and second
    * fields. Other fields are not changed.
    *
    * @param year
    * the year.
    * @param month
    * the month.
    * @param day
    * the day of the month.
    * @param hourOfDay
    * the hour of day.
    * @param minute
    * the minute.
    * @param second
    * the second.
    */
  final def set(year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int): Unit = {
    set(year, month, day, hourOfDay, minute)
    set(Calendar.SECOND, second)
  }

  /**
    * Sets the first day of the week for this {@code Calendar}.
    *
    * @param value
    * a { @code Calendar} day of the week.
    */
  def setFirstDayOfWeek(value: Int): Unit = {
    firstDayOfWeek = value
  }

  /**
    * Sets this {@code Calendar} to accept field values which are outside the valid
    * range for the field.
    *
    * @param value
    * a boolean value.
    */
  def setLenient(value: Boolean): Unit = {
    lenient = value
  }

  /**
    * Sets the minimal days in the first week of the year.
    *
    * @param value
    * the minimal days in the first week of the year.
    */
  def setMinimalDaysInFirstWeek(value: Int): Unit = {
    minimalDaysInFirstWeek = value
  }

  /**
    * Sets the time of this {@code Calendar}.
    *
    * @param date
    * a { @code Date} object.
    */
  final def setTime(date: Date): Unit = {
    setTimeInMillis(date.getTime)
  }

  /**
    * Sets the time of this {@code Calendar}.
    *
    * @param milliseconds
    * the time as the number of milliseconds since Jan. 1, 1970.
    */
  def setTimeInMillis(milliseconds: Long): Unit = {
    if (!isTimeSet || !areFieldsSet || time != milliseconds) {
      time = milliseconds
      isTimeSet = true
      areFieldsSet = false
      complete()
    }
  }

  /**
    * Sets the {@code TimeZone} used by this Calendar.
    *
    * @param timezone
    * a { @code TimeZone}.
    */
  def setTimeZone(timezone: TimeZone): Unit = {
    zone = timezone
    areFieldsSet = false
  }

  /**
    * Returns the string representation of this {@code Calendar}.
    *
    * @return the string representation of this { @code Calendar}.
    */
  override def toString: String = {
    val result = new StringBuilder(getClass.getName + "[time=" + (if (isTimeSet) String.valueOf(time)
    else "?") + ",areFieldsSet=" + areFieldsSet + // ",areAllFieldsSet=" + areAllFieldsSet +
      ",lenient=" + lenient + ",zone=" + zone + ",firstDayOfWeek=" + firstDayOfWeek + ",minimalDaysInFirstWeek=" + minimalDaysInFirstWeek)
    var i = 0
    while ( {
      i < Calendar.FIELD_COUNT
    }) {
      result.append(',')
      result.append(Calendar.fieldNames(i))
      result.append('=')
      if (isSet.apply(i)) result.append(fields(i))
      else result.append('?')

      {
        i += 1; i - 1
      }
    }
    result.append(']')
    result.toString
  }

  /**
    * Compares the times of the two {@code Calendar}, which represent the milliseconds
    * from the January 1, 1970 00:00:00.000 GMT (Gregorian).
    *
    * @param anotherCalendar
    * another calendar that this one is compared with.
    * @return 0 if the times of the two { @code Calendar}s are equal, -1 if the time of
    *                                           this { @code Calendar} is before the other one, 1 if the time of this
    *                                           { @code Calendar} is after the other one.
    * @throws NullPointerException
    * if the argument is null.
    * @throws IllegalArgumentException
    * if the argument does not include a valid time
    * value.
    */
  def compareTo(anotherCalendar: Calendar): Int = {
    if (null == anotherCalendar) throw new NullPointerException
    val timeInMillis = getTimeInMillis
    val anotherTimeInMillis = anotherCalendar.getTimeInMillis
    if (timeInMillis > anotherTimeInMillis) return 1
    if (timeInMillis == anotherTimeInMillis) return 0
    -1
  }

}
