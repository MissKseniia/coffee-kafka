package org.kvlasova.finance_report.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kvlasova.common.entity.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Collections;
import java.util.Properties;

@Configuration
public class FinanceReportConsumerConfig {

    @Bean
    public KafkaConsumer<String, Order> financeReportKafkaConsumer() {
        Properties props = new Properties();

        //Указание брокеров Кафки
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094,localhost:9095,localhost:9096");

        //Определение десериализаторов
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());

        //Выключаем авто коммит
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        //В случае отсутствия - начать с более раннего смещения
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //Разные группы, так как в рамках одной группы
        // консьюмеров каждое сообщение будет обрабатываться только одним из этих экземпляров
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order_for_report");

        //Нужно добавить класс Order в доверяемые пакеты, иначе IllegalStateException
        props.put("spring.json.trusted.packages", "*");
        props.put("spring.kafka.properties.allow.deserialization", "org.kvlasova.common.entity.Order");

        //Добавляем, чтобы набрался данный мин размер данных
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 10 * 1024 * 1024);

        KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(props);
        // Подписка на топик
        consumer.subscribe(Collections.singletonList("t_order"));

        return consumer;
    }
}
