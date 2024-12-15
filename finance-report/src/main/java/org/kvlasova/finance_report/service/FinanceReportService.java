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
import org.springframework.kafka.KafkaException;
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

    private final KafkaConsumer<String, Object> financeReportKafkaConsumer;
    private AtomicInteger ordersNumber = new AtomicInteger(0);
    private EnumMap<Item, Integer> statistics = new EnumMap<>(Item.class);
    private final String REPORT = "\\n---REPORT---\\nTime: %s\\nCurrent Orders' Number: %d\\nCurrent Statistics: %s\\n---REPORT END---\\n";

    public void analyseOrderInfo() {
        Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
        int messageProcessed = 0;
        // Чтение сообщений
        while (true) {
            ConsumerRecords<String, Object> records = financeReportKafkaConsumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) {
                continue;
            }
            for (ConsumerRecord<String, Object> record : records) {
                System.out.printf("Получено сообщение: key = %s, value = %s, offset = %d%n",
                        record.key(), record.value(), record.offset());

                if (record.value().getClass().equals(Order.class)) {
                    Order recievedOrder = (Order) record.value();
                    ordersNumber.incrementAndGet();
                    updateStatistics(recievedOrder);
                    log.info(String.format(REPORT, getTime(), getOrdersNumber(), getStatisticsData()));

                    messageProcessed++;
                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1));
                    if (messageProcessed == records.count()){
                        financeReportKafkaConsumer.commitSync(currentOffsets);
                    }
                } else {
                    throw new KafkaException("Сообщение не является заказом");
                }

            }
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
        recievedOrder.orderItems()
                .forEach(item -> statistics.compute(item, (key, value) -> Objects.isNull(value) ? 1 : value + 1));
    }

}
