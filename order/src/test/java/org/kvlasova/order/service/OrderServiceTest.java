package org.kvlasova.order.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kvlasova.common.entity.Order;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private KafkaProducer<String, Order> orderProducerMock;

    @BeforeEach
    public void setUp() {
        orderService = new OrderService(orderProducerMock);
    }

    @Test
    public void testProcessNewOrder_serializationError() {
        when(orderProducerMock.send(ArgumentMatchers.any(ProducerRecord.class)))
                .thenThrow(new SerializationException("Ошибка при сериализации"));

        orderService.processNewOrder();

        verify(orderProducerMock, times(1)).send(any(ProducerRecord.class));
    }

}