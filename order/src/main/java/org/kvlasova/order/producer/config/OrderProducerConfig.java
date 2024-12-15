package org.kvlasova.order.producer.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

@Configuration
public class OrderProducerConfig {

    @Bean
    public KafkaProducer<String, Object> orderProducer() {
        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "1"); //Гарантия доставки
        properties.put(ProducerConfig.RETRIES_CONFIG, 3); //Количество попыток при ошибке отправки
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 500); // Время ожидания между попытками
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000); // 2 seconds timeout for each request

        // Создание продюсера
        return new KafkaProducer<>(properties);
    }
}
