package com.jpms.codinggame.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class GPTConfig {

    //사용 모델
    public final String MODEL = "gpt-3.5-turbo";



    //생성되는 답변의 최대 길이
    public final static int MAX_TOKEN = 2000;

    //GPT 답변의 다양성과 정확성을 위한 수치: 0.1 과 0.2 는 경직된 답변을 준다
    public final static double TEMPERATURE = 0.2;

    public final static double TOP_P = 0.1;


    //타임아웃 시간 설정
    public final static Duration TIME_OUT = Duration.ofSeconds(300);

    //발급받은 토큰
    @Value("${openai.api.key}")
    private String token;

    @Bean
    public OpenAiService openAiService(){return new OpenAiService(token,TIME_OUT);}

}
