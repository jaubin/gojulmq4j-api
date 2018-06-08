package org.gojul.gojulmq4j.kafka.avroutils;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;

public class GojulDateTimeConverterTest {

    @Test
    public void testDateToEpochDaysWithNullDateReturnNull() {
        assertNull(GojulDateTimeConverter.dateToEpochDays(null));
    }

    @Test
    public void testDateToEpochDays() {
        LocalDate localDate = LocalDate.now();
        LocalDate epoch = LocalDate.ofEpochDay(0L);

        Date d = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        assertEquals((int) ChronoUnit.DAYS.between(epoch, localDate), GojulDateTimeConverter.dateToEpochDays(d).intValue());
    }

    @Test
    public void testEpochDaysToDateWithNullEpochDaysReturnNull() {
        assertNull(GojulDateTimeConverter.epochDaysToDate(null));
    }

    @Test
    public void testEpochDaysToDate() {
        LocalDate localDate = LocalDate.ofEpochDay(42000L);

        Date expected = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        assertEquals(expected, GojulDateTimeConverter.epochDaysToDate(42000));
    }

    @Test
    public void testDateTimeMillisToEpochTime() {
        assertNull(GojulDateTimeConverter.dateTimeMillisToEpochTime(null));
        Date expected = new Date();
        assertEquals(expected.getTime(), GojulDateTimeConverter.dateTimeMillisToEpochTime(expected).longValue());
    }

    @Test
    public void testEpochTimeToDateTimeMillis() {
        assertNull(GojulDateTimeConverter.epochTimeToDateTimeMillis(null));
        Date d = new Date();
        assertEquals(d, GojulDateTimeConverter.epochTimeToDateTimeMillis(d.getTime()));
    }
}