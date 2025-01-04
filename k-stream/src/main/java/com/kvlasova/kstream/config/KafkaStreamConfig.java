package com.kvlasova.kstream.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.Stores;
import org.kvlasova.common.common_enum.Item;
import org.kvlasova.common.entity.Order;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaStreamConfig {

    private final OrderSerde orderSerde = new OrderSerde();

    public KafkaStreams kafkaStreams() {

        Properties properties = new Properties();

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "simple-kafka-streams-app");

        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094,localhost:9095,localhost:9096");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());

        // Создание топологии
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Order> inputStream = builder.stream("t_order", Consumed.with(Serdes.String(), orderSerde));

        // Обработка данных
        KStream<String, Integer> processedStream = inputStream.mapValues(Order::orderTotalAmount)
                .peek((number, amount) -> log.info("\n\nProcessed Order - {} with Amount - {}\n\n", number, amount));

        // Отправка обработанных данных в другой топик
        processedStream.to("t_profit");


        // Создание продюсера
        return new KafkaStreams(builder.build(), properties);
    }

    public KafkaStreams getStatefulKafkaStreams() {

        Properties properties = new Properties();

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "stateful-kafka-streams-app");

        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094,localhost:9095,localhost:9096");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Создание топологии
        StreamsBuilder builder = new StreamsBuilder();

        //Инициализируем state store
//        builder.addStateStore(
//                Stores.keyValueStoreBuilder(
//                        Stores.inMemoryKeyValueStore("order-count-store"),
//                        Serdes.String(), // Key serde
//                        Serdes.Long()    // Value serde
//                )
//        );

        // Создаём KStream из топика с входными данными
        KStream<String, Order> inputStream = builder.stream("t_order",
                Consumed.with(Serdes.String(), orderSerde));

        // Использование метода table() для создания таблицы из Kafka-топика
//        KTable<String, String> table = builder.table(
//                "input-topic", // Название Kafka-топика
//                Consumed.with(Serdes.String(), Serdes.String()) // Указываем сериализаторы для ключа и значения
//        );

        // Пример обработки данных в таблице
        // Метод toStream() преобразует KTable обратно в поток для обработки или отправки в другой поток/топик.
//        table.toStream().foreach((key, value) -> {
//            System.out.println("Key: " + key + ", Value: " + value);
//        });

        // Создание глобальной таблицы из Kafka-топика
//        GlobalKTable<String, String> globalTable = builder.globalTable(
//                "global-topic", // Название Kafka-топика, данные из которого попадут в глобальную таблицу
//                Consumed.with(Serdes.String(), Serdes.String()) // Указываем сериализаторы для ключа и значения
//        );

        // Создание потока для обработки данных
        //KStream<String, String> stream = builder.stream("input-topic", Consumed.with(Serdes.String(), Serdes.String()));

        // Объединение потока с глобальной таблицей по ключу
        // Метод leftJoin объединяет поток (stream) с таблицей (globalTable) на основе ключа.
        // Для каждого ключа из потока (stream) берётся соответствующее значение из таблицы (globalTable).
        // Если в таблице нет совпадения для ключа из потока, результат объединения всё равно включается в поток (left join)
//        KStream<String, String> joinedStream = stream.leftJoin(globalTable,
//                (key, value) -> key,
//                (streamValue, tableValue) -> streamValue + "-" + tableValue);
        // Если tableValue равен null (нет совпадения), результат будет "streamValue-null"

        // Пример обработки объединённых данных
//        joinedStream.foreach((key, value) -> {
//            System.out.println("Joined Key: " + key + ", Value: " + value);
//        });

        // Преобразование потока в таблицу с помощью метода toTable()
        inputStream.flatMapValues(order -> order.orderItems().stream().map(Item::getItemName).toList())  //
                .groupBy((key, item) -> item)  // Группировка по словам
                .count()  // Количество появлений каждого item
                .toStream()  // Вернуть обратно в стрим
                .peek((item, count) -> log.info("\n\nUpdated table: \n Item --> {}, Count: {}\n\n", item, count))
                .toTable(
                        Materialized.<String, Long>as(Stores.persistentKeyValueStore("order-count-store"))
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Long()) // Сериализаторы для ключей и значений (в этом случае – строки).
                );
                //.to("t_items");  // Отправить в топик для результатов


        // Создание продюсера
        return new KafkaStreams(builder.build(), properties);
    }
}
