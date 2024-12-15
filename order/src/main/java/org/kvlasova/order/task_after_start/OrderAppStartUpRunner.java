package org.kvlasova.order.task_after_start;

import lombok.RequiredArgsConstructor;
import org.kvlasova.order.service.OrderService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class OrderAppStartUpRunner implements ApplicationRunner {

    private final OrderService orderService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Random random = new Random();
        for (int i = 0; i < 51; i++) {
            orderService.processNewOrder();
            Thread.sleep(random.nextInt(1000, 10000));
        }
    }
}
