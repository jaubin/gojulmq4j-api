package org.gojul.gojulmq4j.utils.avro;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GojulAvroDecimalConverterTest {

    @Test
    public void testDecimalToByteBufferWithNullValueReturnsNull() {
        assertNull(GojulAvroDecimalConverter.decimalToByteBuffer(null));
    }

    @Test
    public void testDecimalToByteBuffer() {
        BigDecimal bd = new BigDecimal(42.0);
        ByteBuffer expected = ByteBuffer.wrap(bd.setScale(GojulAvroDecimalConverter.AVRO_BIGDECIMAL_SCALE, RoundingMode.HALF_EVEN)
            .unscaledValue().toByteArray());

        assertEquals(expected, GojulAvroDecimalConverter.decimalToByteBuffer(bd));

    }
}