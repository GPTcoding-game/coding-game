package com.jpms.codinggame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodingGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodingGameApplication.class, args);
    }

}
