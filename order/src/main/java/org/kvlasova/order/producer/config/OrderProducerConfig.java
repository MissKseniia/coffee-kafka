package org.kvlasova.order.producer.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.kvlasova.common.entity.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

@Configuration
public class OrderProducerConfig {

    @Bean
    public KafkaProducer<String, Order> orderProducer() {

        Properties properties = new Properties();

        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094,localhost:9095,localhost:9096");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        //Гарантия доставки как минимум до лидера
        properties.put(ProducerConfig.ACKS_CONFIG, "1");

        //Количество попыток при ошибке отправки
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Время ожидания (в миллисекундах) между повторными попытками отправки
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 500);

        // Время в миллисекундах, в течение которого клиент ожидает ответа от сервера на запрос.
        // Если клиент не получит ответ от брокера в течение 2 секунд, он прекратит попытки,
        // и вся операция отправки сообщения завершится с ошибкой (если (RETRIES_CONFIG) превышен)
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);

        // Создание продюсера
        return new KafkaProducer<>(properties);
    }
}
