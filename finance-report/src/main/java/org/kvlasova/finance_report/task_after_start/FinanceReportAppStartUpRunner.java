package org.kvlasova.finance_report.task_after_start;

import lombok.RequiredArgsConstructor;
import org.kvlasova.finance_report.service.FinanceReportService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinanceReportAppStartUpRunner implements ApplicationRunner {

    private final FinanceReportService financeReportService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        financeReportService.analyseOrderInfo();
    }
}
