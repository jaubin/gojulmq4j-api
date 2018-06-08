package org.gojul.gojulmq4j.utils.avro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;

/**
 * <p>
 * Class {@code GojulAvroDecimalConverter} contains various utilities that
 * make it easier to convert decimal numbers like {@link BigDecimal} to
 * Avro classes.
 * </p>
 *
 * @author jaubin
 */
public class GojulAvroDecimalConverter {

    /**
     * The fixed scale we use for BigDecimal encoding / decoding. We
     * fix it to make it easier to make it easier to convert BigDecimals.
     */
    public final static int AVRO_BIGDECIMAL_SCALE = 5;

    /**
     * Private constructor. Prevents the class from being instanciated.
     */
    private GojulAvroDecimalConverter() {

    }

    /**
     * Convert the {@link BigDecimal} instance {@code bigDecimal} to a
     * target {@link ByteBuffer}, or {@code null} if {@code bigDecimal}
     * is {@code null}. Note that if {@code bigDecimal} scale exceeds {@link GojulAvroDecimalConverter#AVRO_BIGDECIMAL_SCALE}
     * it is scaled down to that value, using rounding mode {@link RoundingMode#HALF_EVEN}.
     *
     * @param bigDecimal the value to convert.
     * @return the converted value.
     */
    public static ByteBuffer decimalToByteBuffer(final BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }

        BigDecimal bdConvert = bigDecimal.setScale(AVRO_BIGDECIMAL_SCALE, RoundingMode.HALF_EVEN);
        return ByteBuffer.wrap(bdConvert.unscaledValue().toByteArray());
    }
}
