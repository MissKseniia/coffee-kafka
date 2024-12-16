package org.kvlasova.payment.service;

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

    public void processPayment() {
        // Чтение сообщений
        try(paymentConsumer) {
            while (ordersNumber.get() != 10) {

                ConsumerRecords<String, Order> records = paymentConsumer.poll(Duration.ofMillis(20));
                if (records.isEmpty()) {
                    continue;
                }
                for (ConsumerRecord<String, Order> record : records) {
                    System.out.printf("Время: %s \nПолучено сообщение: \nkey = %s, \nvalue = %s, \noffset = %d",
                            Instant.now().toString(), record.key(), record.value(), record.offset());
                    System.out.printf("\nЗаказ: key = %s \n--- оплачен\n", record.key());
                    ordersNumber.incrementAndGet();

                    for (TopicPartition tp : paymentConsumer.assignment()) {
                        long offset = paymentConsumer.position(tp);
                        System.out.printf("Partition: %d, Offset: %d\n\n", tp.partition(), offset);
                    }
                }
            }
        } catch (Exception de) {
            System.err.printf("При обработке сообщения возникла ошибка: %s\n", de.getMessage());
        }
    }
}