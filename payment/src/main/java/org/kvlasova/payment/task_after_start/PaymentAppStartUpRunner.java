package org.kvlasova.payment.task_after_start;

import lombok.RequiredArgsConstructor;
import org.kvlasova.payment.service.PaymentService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentAppStartUpRunner implements ApplicationRunner {

    private final PaymentService paymentService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        paymentService.processPayment();
    }
}
