package org.gojul.gojulmq4j.utils.avro;

import java.math.BigDecimal;
import java.math.BigInteger;
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
     * <p>
     * The fixed scale we use for BigDecimal encoding / decoding. We
     * fix it to make it easier to make it easier to convert BigDecimals.
     * </p>
     * <p>
     * <b>WARNING</b> : this value must not change over time, otherwise breakages
     * will happen in transmitted messages ! This is the reason why we've put it to a high value.
     * </p>
     */
    public final static int AVRO_BIGDECIMAL_SCALE = 50;

    /**
     * Private constructor. Prevents the class from being instanciated.
     */
    private GojulAvroDecimalConverter() {
        throw new IllegalStateException("Go away !!!");
    }

    /**
     * Convert the {@link BigDecimal} instance {@code bigDecimal} to a
     * target {@link ByteBuffer}, or return {@code null} if {@code bigDecimal}
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

    /**
     * <p>
     * Convert the {@link ByteBuffer} instance {@code byteBuffer} to a
     * target {@link BigDecimal} instance, or return {@code null} if
     * {@code byteBuffer} is {@code null}.
     * </p>
     * <p><b>WARNING !</b> the encoded decimals must have their scale set to {@link GojulAvroDecimalConverter#AVRO_BIGDECIMAL_SCALE},
     * otherwise data loss will occur !</p>
     *
     * @param byteBuffer the value to convert.
     * @return the converted value.
     */
    public static BigDecimal byteBufferToDecimal(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }

        byteBuffer.rewind();
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);

        return new BigDecimal(new BigInteger(data), AVRO_BIGDECIMAL_SCALE);
    }
}
