package com.kvlasova.kstream.task_after_start;

import com.kvlasova.kstream.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaStreamAppStartUpRunner implements ApplicationRunner {

    private final StreamService streamService;

    @Override
    public void run(ApplicationArguments args) {
        streamService.countProfit();
    }
}
