package com.jpms.codinggame.service;

import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.global.dto.GPTRequestDto;
import com.jpms.codinggame.global.dto.GPTResponseDto;
import com.jpms.codinggame.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class GPTService {

    private final QuestionRepository questionRepository;

    public String createPrompt(){
        String prompt = "자료구조 중에 스택에 관한 자바 코딩 문제를 내줘.\n" +
                "답이 100자가 넘어가지 않는 단답형으로 컴파일시 결과를 물어보거나 코드 중 빈칸을 체워 넣는 형식으로 부탁해\n" +
                "\n" +
                "형식은\n" +
                "문제: 다음에 문제를 적어주고\n" +
                "답: 다음에 답을 적어줘";

        return prompt;
    }
    public Question createQuestion(String model, String apiURL, RestTemplate template){
        String prompt = createPrompt();
        int maxTokens = 2000;
        GPTRequestDto request = new GPTRequestDto(model, prompt, maxTokens);
        GPTResponseDto chatGPTResponse =  template.postForObject(apiURL, request, GPTResponseDto.class);
        String content = chatGPTResponse.getChoices().get(0).getMessage().getContent();

        String question = null;
        String answer = null;

        // 정규 표현식을 이용하여 문제와 답을 추출
        Pattern pattern = Pattern.compile("문제:(.*)답:(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            question = matcher.group(1).trim();
            answer = matcher.group(2).trim();
        }

        //보완로직
        //선행학습

        Question question1 = questionRepository.save(Question.builder().
                content(question).
                answer(answer).
                date(LocalDate.now()).
                build());

        return question1;
    }

}
