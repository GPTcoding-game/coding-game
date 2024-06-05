package com.jpms.codinggame.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Configuration
public class GPTConfig {


    @Value("${openai.model}")
    public static String model;

    @Value("${openai.api.url}")
    public static String apiURL;

    @Autowired
    public static RestTemplate template;

    //타임아웃 시간 설정
    public final static Duration TIME_OUT = Duration.ofSeconds(300);

    //발급받은 토큰
    @Value("${openai.api.key}")
    private String token;

    @Bean
    public OpenAiService openAiService(){return new OpenAiService(token,TIME_OUT);}

}
