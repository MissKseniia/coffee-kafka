package org.kvlasova.payment.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.kvlasova.common.entity.Order;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final KafkaConsumer<String, Order> paymentConsumer;
    private AtomicInteger ordersNumber = new AtomicInteger(0);

    @PreDestroy
    public void destroy() {
        paymentConsumer.close();
    }

    public void processPayment() {
        // Чтение сообщений
        while (ordersNumber.get() != 10) {
            try {
                ConsumerRecords<String, Order> records = paymentConsumer.poll(Duration.ofMillis(20));
                if (records.isEmpty()) {
                    continue;
                }
                for (ConsumerRecord<String, Order> record : records) {
                    System.out.printf("Время: %s \nПолучено сообщение: \nkey = %s, \nvalue = %s, \noffset = %d",
                            Instant.now().toString(), record.key(), record.value(), record.offset());
                    System.out.printf("\nЗаказ: key = %s \n--- оплачен", record.key());

                    for (TopicPartition tp : paymentConsumer.assignment()) {
                        long offset = paymentConsumer.position(tp);
                        System.out.println("Partition: " + tp.partition() + ", Offset: \n" + offset);
                    }
                }
            } catch (Exception de) {
                System.err.printf("При обработке сообщения возникла ошибка: %s\n", de.getMessage());
            } finally {
                if (ordersNumber.incrementAndGet() == 10) {
                    paymentConsumer.close();
                }
            }
        }
    }
}