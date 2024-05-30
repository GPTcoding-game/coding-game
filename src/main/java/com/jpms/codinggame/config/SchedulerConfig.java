package com.jpms.codinggame.config;

import com.jpms.codinggame.service.GPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SchedulerConfig {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @Autowired
    private GPTService gptService;

    @Scheduled(cron = "0 0 3 * * ?")
    public void perform() throws Exception {
        for (int i = 0; i < 20; i++) {
            try {
                gptService.createQuestion(model, apiURL, template);
            } catch (Exception e) {
                System.err.println("문제 생성 실패: " + e.getMessage());
            }
        }
    }
}