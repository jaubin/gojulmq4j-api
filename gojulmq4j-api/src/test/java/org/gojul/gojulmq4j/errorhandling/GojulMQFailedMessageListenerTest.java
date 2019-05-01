package org.gojul.gojulmq4j.errorhandling;

import org.gojul.gojulmq4j.GojulMQException;
import org.gojul.gojulmq4j.GojulMQMessageKeyProvider;
import org.gojul.gojulmq4j.GojulMQMessageListener;
import org.gojul.gojulmq4j.GojulMQMessageProducer;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GojulMQFailedMessageListenerTest {

    private GojulMQMessageProducer<Integer> producer;
    private GojulMQMessageListener<Integer> listener;
    private GojulMQFailedMessageListener<Integer> failedMessageListener;

    @Before
    public void setUp() throws Exception {
        producer = mock(GojulMQMessageProducer.class);
        listener = mock(GojulMQMessageListener.class);
        failedMessageListener = new GojulMQFailedMessageListener<Integer>(producer, listener, "hello");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullProducerThrowsException() {
        new GojulMQFailedMessageListener<>(null, mock(GojulMQMessageListener.class), "hello");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullListenerThrowsException() {
        new GojulMQFailedMessageListener<>(mock(GojulMQMessageProducer.class), null, "hello");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullTopicNameThrowsException() {
        new GojulMQFailedMessageListener<>(mock(GojulMQMessageProducer.class), mock(GojulMQMessageListener.class), null);
    }

    @Test
    public void testOnMessageWithoutExceptionDoesNothing() {
        failedMessageListener.onMessage(42);

        verify(listener).onMessage(42);
        verifyNoMoreInteractions(producer);
    }

    @Test(expected = GojulMQException.class)
    public void testOnMessageWithMQExceptionForwardsException() {
        GojulMQMessageListener<Integer> errorListener = new GojulMQMessageListener<Integer>() {
            @Override
            public void onMessage(Integer message) {
                throw new GojulMQException();
            }
        };
        GojulMQMessageListener<Integer> hospitalListener = new GojulMQFailedMessageListener<>(producer, errorListener,
                "hello");
        hospitalListener.onMessage(42);
    }

    @Test
    public void testOnMessageWithExceptionSendsToHospital() {
        GojulMQMessageListener<Integer> errorListener = new GojulMQMessageListener<Integer>() {
            @Override
            public void onMessage(Integer message) {
                throw new IllegalArgumentException();
            }
        };
        GojulMQMessageListener<Integer> hospitalListener = new GojulMQFailedMessageListener<>(producer, errorListener,
                "hello");
        hospitalListener.onMessage(42);

        verify(producer).sendMessage(eq("hello"), any(GojulMQMessageKeyProvider.class), eq(42));
    }
}