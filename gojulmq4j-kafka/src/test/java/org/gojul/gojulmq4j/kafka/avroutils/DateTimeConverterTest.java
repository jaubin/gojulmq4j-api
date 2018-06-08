package org.gojul.gojulmq4j.kafka.avroutils;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;

public class DateTimeConverterTest {

    @Test
    public void testDateToEpochDaysWithNullDateReturnNull() {
        assertNull(DateTimeConverter.dateToEpochDays(null));
    }

    @Test
    public void testDateToEpochDays() {
        LocalDate localDate = LocalDate.now();
        LocalDate epoch = LocalDate.ofEpochDay(0L);

        Date d = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        assertEquals((int) ChronoUnit.DAYS.between(epoch, localDate), DateTimeConverter.dateToEpochDays(d).intValue());
    }
}