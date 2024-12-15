package org.kvlasova.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.kvlasova.common.common_enum.Item;
import org.kvlasova.common.common_enum.OrderStatus;
import org.kvlasova.common.entity.Order;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final KafkaProducer<String, Order> orderProducer;
    private final String T_ORDER_TOPIC = "t_order";

    //метод по обработке заказа
    public void processNewOrder(){
        //генерируем заказ
        var order = generateOrder();
        //формируем сообщение
        ProducerRecord<String, Order> orderRecord = new ProducerRecord<>(T_ORDER_TOPIC, order.orderNumber(), order);
        //отправляем сообщение
        try {
            var result = orderProducer.send(orderRecord);
            if (Objects.nonNull(result) && result.get().hasOffset()) {
                System.out.printf("Время: %s\nСообщение успешно отправлено в топик %s: \n%s\n", Instant.now().toString(),
                        result.get().topic(), orderRecord);
            }
        } catch (Exception e) {
            System.err.printf("Возникла ошибка при отправке сообщения: %s\n", e.getMessage());
        }

    }

    //метод по генерации заказа
    private Order generateOrder() {
        RandomGenerator randomGenerator = new Random();
        List<Item> orderItems = generateOrderItems(randomGenerator);

        return new Order(
                String.format("%d", Math.abs(randomGenerator.nextInt())),
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

    public void orderProducerClose() {
        orderProducer.close();
    }
}
