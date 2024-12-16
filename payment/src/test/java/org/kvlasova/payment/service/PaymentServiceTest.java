package org.kvlasova.payment.service;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kvlasova.common.entity.Order;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.serializer.DeserializationException;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private PaymentService paymentService;
    @Mock
    private KafkaConsumer<String, Order> paymentKafkaConsumer;

    @BeforeEach
    public void setUp() {
        paymentService = new PaymentService(paymentKafkaConsumer);
    }

    @Test
    public void testProcessPayment_deserializationError() {
        when(paymentKafkaConsumer.poll(any(Duration.class)))
                .thenThrow(new DeserializationException("Ошибка при десериализации", new byte[0], true, any(Throwable.class)));

        paymentService.processPayment();

        verify(paymentKafkaConsumer, times(1)).poll(any(Duration.class));
    }
}