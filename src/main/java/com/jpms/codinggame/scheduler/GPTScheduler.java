package com.jpms.codinggame.scheduler;

import com.jpms.codinggame.config.GPTConfig;
import com.jpms.codinggame.controller.GPTController;
import com.jpms.codinggame.service.GPTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class GPTScheduler {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;


    private final GPTService gptService;
    @Scheduled(cron = "0 0 0 * * ?")
    public void createQuestionsAtMidnight() {
        for (int i = 0; i < 50; i++) {
            try {
                gptService.createQuestion(model, apiURL, template);
            } catch (Exception e) {
                // 예외 처리: 로깅 또는 재시도 로직 추가 가능
                e.printStackTrace();
            }
        }
    }
}
