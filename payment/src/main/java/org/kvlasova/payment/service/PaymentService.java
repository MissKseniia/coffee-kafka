package org.kvlasova.payment.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.kvlasova.common.entity.Order;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final KafkaConsumer<String, Object> paymentConsumer;

    @PreDestroy
    public void destroy() {
        paymentConsumer.close();
    }

    public void processPayment() {
        // Чтение сообщений
        while (true) {
            ConsumerRecords<String, Object> records = paymentConsumer.poll(Duration.ofMillis(20));
            if (records.isEmpty()) {
                continue;
            }
            for (ConsumerRecord<String, Object> record : records) {
                System.out.printf("Получено сообщение: key = %s, value = %s, offset = %d%n",
                        record.key(), record.value(), record.offset());

                if (record.value().getClass().equals(Order.class)) {
                    System.out.printf("Заказ: key = %s --- оплачен", record.key());
                } else {
                    throw new KafkaException("Сообщение не является заказом");
                }

            }
        }
    }
}
