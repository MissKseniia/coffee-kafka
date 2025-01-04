package com.kvlasova.kstream.service;

import com.kvlasova.kstream.config.KafkaStreamConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {

    private final KafkaStreamConfig kafkaStreamConfig;

    //метод по обработке заказа
    public void countProfit(){
        try {
            var stream = kafkaStreamConfig.kafkaStreams();
            var statefulStream = kafkaStreamConfig.getStatefulKafkaStreams();

            stream.start();
            statefulStream.start();
            System.out.println("Kafka Streams приложение запущено успешно.");
            Runtime.getRuntime().addShutdownHook(new Thread(stream::close));
            Runtime.getRuntime().addShutdownHook(new Thread(statefulStream::close));
        } catch (Exception e) {
            System.err.println("Ошибка при запуске Kafka Streams приложения: " + e.getMessage());
        }

    }

}
