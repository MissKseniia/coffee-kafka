package org.kvlasova.finance_report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.kvlasova.common.common_enum.Item;
import org.kvlasova.common.entity.Order;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceReportService {

    private final KafkaConsumer<String, Order> financeReportKafkaConsumer;
    private AtomicInteger ordersNumber = new AtomicInteger(0);
    private AtomicInteger profit = new AtomicInteger(0);
    private EnumMap<Item, Integer> statistics = new EnumMap<>(Item.class);
    private final String REPORT = "\n---REPORT---\nCurrent Orders' number: %d\nCurrent Profit: %d\nCurrent Statistics: %s\n---REPORT END---\n";

    public void analyseOrderInfo() {
        Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
        // Чтение сообщений
        try (financeReportKafkaConsumer) {
            while (ordersNumber.get() != 10) {

                ConsumerRecords<String, Order> records = financeReportKafkaConsumer.poll(Duration.ofMillis(120));
                if (records.isEmpty()) {
                    continue;
                }
                for (ConsumerRecord<String, Order> record : records) {
                    System.out.printf("Время: %s\nПолучено сообщение: \nkey = %s, \nvalue = %s, \noffset = %d\n",
                            getTime(), record.key(), record.value(), record.offset());

                    updateStatistics(record.value());

                    System.out.printf(String.format(REPORT, getOrdersNumber(), profit.get(), getStatisticsData()));

                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1));

                    for (TopicPartition tp : financeReportKafkaConsumer.assignment()) {
                        long offset = financeReportKafkaConsumer.position(tp);
                        System.out.printf("Partition: %d, Offset: %d\n\n", tp.partition(), offset);
                    }
                }

                //фиксируем смещение
                financeReportKafkaConsumer.commitSync(currentOffsets);
                currentOffsets.clear();
            }
        } catch (Exception e) {
            System.err.printf("При обработке сообщения возникла ошибка: %s\n", e.getMessage());
            financeReportKafkaConsumer.close();
        }
    }

    private String getTime() {
        return Instant.now().toString();
    }

    private Integer getOrdersNumber() {
        return ordersNumber.get();
    }

    private String getStatisticsData() {
        return statistics.toString();
    }

    private void updateStatistics(Order recievedOrder) {
        ordersNumber.incrementAndGet();
        profit.getAndAdd(recievedOrder.orderTotalAmount());
        recievedOrder.orderItems()
                .forEach(item -> statistics.compute(item, (key, value) -> Objects.isNull(value) ? 1 : value + 1));
    }

}
