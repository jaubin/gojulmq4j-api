package org.gojul.gojulmq4j.utils.avro;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GojulAvroDateTimeConverterTest {

    @Test
    public void testDateToEpochDaysWithNullDateReturnNull() {
        assertNull(GojulAvroDateTimeConverter.dateToEpochDays(null));
    }

    @Test
    public void testDateToEpochDays() {
        LocalDate localDate = LocalDate.now();
        LocalDate epoch = LocalDate.ofEpochDay(0L);

        Date d = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        assertEquals((int) ChronoUnit.DAYS.between(epoch, localDate), GojulAvroDateTimeConverter.dateToEpochDays(d).intValue());
    }

    @Test
    public void testEpochDaysToDateWithNullEpochDaysReturnNull() {
        assertNull(GojulAvroDateTimeConverter.epochDaysToDate(null));
    }

    @Test
    public void testEpochDaysToDate() {
        LocalDate localDate = LocalDate.ofEpochDay(42000L);

        Date expected = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        assertEquals(expected, GojulAvroDateTimeConverter.epochDaysToDate(42000));
    }

    @Test
    public void testDateTimeMillisToEpochTime() {
        assertNull(GojulAvroDateTimeConverter.dateTimeMillisToEpochTime(null));
        Date expected = new Date();
        assertEquals(expected.getTime(), GojulAvroDateTimeConverter.dateTimeMillisToEpochTime(expected).longValue());
    }

    @Test
    public void testEpochTimeToDateTimeMillis() {
        assertNull(GojulAvroDateTimeConverter.epochTimeToDateTimeMillis(null));
        Date d = new Date();
        assertEquals(d, GojulAvroDateTimeConverter.epochTimeToDateTimeMillis(d.getTime()));
    }
}