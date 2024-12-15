package org.kvlasova.order.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.kvlasova.common.common_enum.Item;
import org.kvlasova.common.common_enum.OrderStatus;
import org.kvlasova.common.entity.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final KafkaProducer<String, Object> orderProducer;
    private final String T_ORDER_TOPIC = "t_new_order";

    @PostConstruct


    //метод по обработке заказа
    public void processNewOrder(){
        //генерируем заказ
        var order = generateOrder();
        //формируем сообщение
        ProducerRecord<String, Object> orderRecord = new ProducerRecord<>(T_ORDER_TOPIC, order.orderNumber(), order);
        //отправляем сообщение
        try {
            System.out.printf("Сообщение успешно отправлено в топик %s: \n%s\n", orderRecord.topic(), orderRecord);
            orderProducer.send(orderRecord);
        } catch (Exception e) {
            System.err.printf("Возникла ошибка при отправке сообщения: %s\n", e.getMessage());
        } finally {
            //закрываем продюсера
            orderProducer.close();
        }
    }

    //метод по генерации заказа
    private Order generateOrder() {
        RandomGenerator randomGenerator = new Random();
        List<Item> orderItems = generateOrderItems(randomGenerator);

        return new Order(
                String.format("%d", randomGenerator.nextInt()),
                calculateTotalAmount(orderItems),
                OrderStatus.IS_NOT_PAID,
                orderItems
        );
    }

    //метод по подсчету стоимости заказа
    private int calculateTotalAmount(List<Item> orderItems){
        return orderItems.stream()
                .mapToInt(Item::getItemPrice)
                .sum();
    }

    //метод по генерации элементов заказа
    private List<Item> generateOrderItems(RandomGenerator randomGenerator) {
        var items = Item.values();
        return randomGenerator.ints(randomGenerator.nextLong(1, 4), 0, items.length)
                    .mapToObj(random -> items[random])
                    .toList();
    }
}
