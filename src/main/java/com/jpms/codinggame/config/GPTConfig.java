package com.jpms.codinggame.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class GPTConfig {

    //사용 모델
    public final String MODEL = "gpt-3.5-turbo";

    public final static double TOP_P = 0.1;

    //생성되는 답변의 최대 길이
    public final static int MAX_TOKEN = 2000;

    public final static double TEMPERATURE = 0.2;

    //타임아웃 시간 설정
    public final static Duration TIME_OUT = Duration.ofSeconds(300);

    //발급받은 토큰
    @Value("sk-proj-ZWDJaIBSPDGijg9Cus4lT3BlbkFJFdg7zkTt9iqgRnH1ywqn")
    private String token;

    @Bean
    public OpenAiService openAiService(){return new OpenAiService(token,TIME_OUT)};

}
