package org.kvlasova.finance_report.service;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kvlasova.common.entity.Order;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.serializer.DeserializationException;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceReportServiceTest {

    private FinanceReportService financeReportService;

    @Mock
    private KafkaConsumer<String, Order> financeReportConsumer;

    @BeforeEach
    public void setUp() {
        financeReportService = new FinanceReportService(financeReportConsumer);
    }

    @Test
    public void testAnalyseOrderInfo_deserializationError() {
        when(financeReportConsumer.poll(any(Duration.class)))
                .thenThrow(new DeserializationException("Ошибка при десериализации", new byte[0], true, any(Throwable.class)));

        financeReportService.analyseOrderInfo();

        verify(financeReportConsumer, times(1)).poll(any(Duration.class));
    }

}