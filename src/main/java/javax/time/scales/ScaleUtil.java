/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javax.time.scales;

import javax.time.Duration;
import javax.time.MathUtils;
import javax.time.TimeScaleInstant;
import javax.time.TimeScaleInstant.Validity;

/** Common utility methods for TimeScale's.
 *
 * @author Mark Thornton
 */
public class ScaleUtil {
    /** 1e9 */
    public static final int NANOS_PER_SECOND = 1000000000;
	private static long SECONDS_PER_DAY = 86400;
    /** Modified Julian Day of 1970-01-01 */
	public static int MJD_EPOCH = modifiedJulianDay(1970, 1, 1);
    /** Seconds from epoch of 1958-01-01. */
    public static final long START_TAI = epochSeconds(1958, 1, 1);
    /** Seconds from epoch of 1972-01-01.
     From this date UTC seconds are SI seconds and the value of TAI-UTC is always an integer
     */
    public static final long START_LEAP_SECONDS = epochSeconds(1972, 1, 1);
    /** Start of leap seconds in TAI.*/
    public static final long TAI_START_LEAP_SECONDS = START_LEAP_SECONDS+10;

    private ScaleUtil() {
    }

    /** adjust result of addition/subtraction around gaps .*/
    static TimeScaleInstant adjustUTCAroundGaps(TimeScaleInstant t, long resultEpochSeconds, int resultNanoOfSecond) {
        EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().entryFromUTC(resultEpochSeconds);
        if (resultEpochSeconds+1 == e.getEndEpochSeconds() && resultNanoOfSecond-e.getUTCGapNanoseconds() > ScaleUtil.NANOS_PER_SECOND) {
            // result is within invalid interval
            int z;
            if (t.getEpochSeconds() < resultEpochSeconds)
                z = -1;
            else if (t.getEpochSeconds() > resultEpochSeconds)
                z = 1;
            else
                z = t.getNanoOfSecond() - resultNanoOfSecond;
            // if the result is greater than the initial value, add some more to get to the end of the gap
            // if the result is less than the initial value, take of some more to reach the beginning of the gap
            if (z <= 0) {
                // advance to end of gap
                resultEpochSeconds++;
                resultNanoOfSecond = 0;
            }
            else {
                // go back to beginning of gap
                resultNanoOfSecond = ScaleUtil.NANOS_PER_SECOND + e.getUTCGapNanoseconds();
            }
        }
        return TimeScaleInstant.seconds(t.getTimeScale(), resultEpochSeconds, 0, resultNanoOfSecond);
    }

    static TimeScaleInstant tai(long utcEpochSeconds, int nanoOfSecond) {
        if (utcEpochSeconds < START_TAI) {
            // trivial case
            return TimeScaleInstant.seconds(TAI.INSTANCE, utcEpochSeconds, nanoOfSecond);
        }
        if (utcEpochSeconds < START_LEAP_SECONDS) {
            return fromEarlyInstant(utcEpochSeconds, nanoOfSecond);
        }
        return fromModernInstant(utcEpochSeconds, 0, nanoOfSecond);
    }

    static TimeScaleInstant tai(long utcEpochSeconds, int leapSecond, int nanoOfSecond) {
        if (utcEpochSeconds < START_TAI) {
            // trivial case
            return TimeScaleInstant.seconds(TAI.INSTANCE, utcEpochSeconds, nanoOfSecond);
        }
        if (utcEpochSeconds < START_LEAP_SECONDS) {
            return fromEarlyInstant(utcEpochSeconds, nanoOfSecond);
        }
        return fromModernInstant(utcEpochSeconds, leapSecond, nanoOfSecond);
    }

    private static TimeScaleInstant fromEarlyInstant(long utcEpochSeconds, int nanoOfSecond) {
        EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().entryFromUTC(utcEpochSeconds);
        long nanos = nanoOfSecond + e.getUTCDeltaNanoseconds(utcEpochSeconds, nanoOfSecond);
        TimeScaleInstant ts =  TimeScaleInstant.seconds(TAI.INSTANCE,
           MathUtils.safeAdd(utcEpochSeconds, nanos/ScaleUtil.NANOS_PER_SECOND),
           (int)(nanos%ScaleUtil.NANOS_PER_SECOND));
        if (e.getNext() != null && ts.isAfter(e.getNext().getStartTAI())) {
            // The source instant lies within a non existant range
            ts = e.getNext().getStartTAI();
        }
        return ts;
    }

    private static TimeScaleInstant fromModernInstant(long utcEpochSeconds, int leapSecond, int nanoOfSecond) {
        LeapSeconds.Entry e = LeapSeconds.list().entryFromUTC(utcEpochSeconds);
        return TimeScaleInstant.seconds(TAI.INSTANCE, MathUtils.safeAdd(utcEpochSeconds, e.getDeltaSeconds()+leapSecond), nanoOfSecond);
    }

    static Validity checkEarlyValidity(TimeScaleInstant instant) {
        EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().entryFromUTC(instant.getEpochSeconds());
        // gaps/overlap occur within the last second so quickly reject other cases
        int gap = e.getUTCGapNanoseconds();
        if (gap == 0 || instant.getEpochSeconds() < e.getEndEpochSeconds()-1) {
            return Validity.valid;
        }
        if (instant.getNanoOfSecond() <= NANOS_PER_SECOND-Math.abs(gap))
            return Validity.valid;
        return gap < 0 ? Validity.invalid : Validity.ambiguous;
    }

    public static TimeScaleInstant simpleAdd(TimeScaleInstant t, Duration d) {
        if (t.getTimeScale().supportsLeapSecond())
            throw new UnsupportedOperationException("simpleAdd does not support timescales with leap seconds");
        long seconds = d.getSeconds();
        int nanos = d.getNanosInSecond();
        if (seconds == 0 && nanos == 0)
            return t;
        seconds = MathUtils.safeAdd(t.getEpochSeconds(), seconds);
        nanos += t.getNanoOfSecond();
        if (nanos >= NANOS_PER_SECOND) {
            nanos -= NANOS_PER_SECOND;
            seconds = MathUtils.safeIncrement(seconds);
        }
        return TimeScaleInstant.seconds(t.getTimeScale(), seconds, nanos);
    }

    public static TimeScaleInstant simpleSubtract(TimeScaleInstant t, Duration d) {
        if (t.getTimeScale().supportsLeapSecond())
            throw new UnsupportedOperationException("simpleAdd does not support timescales with leap seconds");
        long seconds = d.getSeconds();
        int nanos = d.getNanosInSecond();
        if (seconds == 0 && nanos == 0)
            return t;
        seconds = MathUtils.safeSubtract(t.getEpochSeconds(), seconds);
        nanos = t.getNanoOfSecond() - nanos;
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        return TimeScaleInstant.seconds(t.getTimeScale(), seconds, nanos);
    }

    public static Duration durationBetween(TimeScaleInstant start, TimeScaleInstant end) {
        if (start.getTimeScale() != end.getTimeScale())
            throw new IllegalArgumentException("Start and end must be on the same time scale");
        long secs = MathUtils.safeSubtract(end.getEpochSeconds(), start.getEpochSeconds());
        int nanos = end.getNanoOfSecond() - start.getNanoOfSecond();
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return Duration.seconds(secs, nanos);
    }

    public static int julianDayNumber(int year, int month, int day) {
        int a = (14-month)/12;
        int y = year+4800-a;
        int m = month+12*a-3;
        return day + (153*m+2)/5 + 365*y + y/4 - y/100 + y/400 - 32045;
    }

    public static int modifiedJulianDay(int year, int month, int day) {
        return julianDayNumber(year, month, day) - 2400001;
    }

    public static long epochSeconds(int year, int month, int day) {
        return SECONDS_PER_DAY*(modifiedJulianDay(year, month, day) - MJD_EPOCH);
    }
}
