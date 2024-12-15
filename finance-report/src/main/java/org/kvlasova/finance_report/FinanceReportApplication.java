package org.kvlasova.finance_report;

import lombok.RequiredArgsConstructor;
import org.kvlasova.common.entity.Order;
import org.kvlasova.finance_report.service.FinanceReportService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@SpringBootApplication
public class FinanceReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceReportApplication.class, args);
    }

}
