package com.jpms.codinggame.service;

import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.global.dto.GPTRequestDto;
import com.jpms.codinggame.global.dto.GPTResponseDto;
import com.jpms.codinggame.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
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
        String prompt = "자바 코딩 문제를 생성해 주세요. 주제는 자료구조 중 '스택'입니다.\n" +
                "코드를 제공하고, 컴파일 시 결과를 물어보거나 코드 중 빈칸을 채우는 형식이어야 합니다.\n" +
                "5가지의 보기가 주어지고 그 중 답을 고르는 문제입니다.\n" +
                "다음 형식을 따라 작성해 주세요:\n" +
                "문제: [여기에 문제를 적어 주세요]\n" +
                "보기: [여기에 보기 5가지를 적어 주세요]\n" +
                "답: [여기에 숫자로 된 답을 적어 주세요]";

        return prompt;
    }
    public Question createQuestion(String model, String apiURL, RestTemplate template) {
        String question = null;
        String answer = null;
        String content = null;
        Pattern pattern = Pattern.compile("문제:(.*)답:(.*)", Pattern.DOTALL);

        //시도 횟수
        int attempt = 0;
        final int maxAttempts = 5;

        while ((question == null || answer == null || !isValidAnswer(answer)) && attempt < maxAttempts) {

            String prompt = createPrompt();

            if (attempt > 0) {
                prompt = modifyPrompt(prompt, question, answer);
            }

            int maxTokens = 2000;
            GPTRequestDto request = new GPTRequestDto(model, prompt, maxTokens);
            GPTResponseDto chatGPTResponse = template.postForObject(apiURL, request, GPTResponseDto.class);
            content = chatGPTResponse.getChoices().get(0).getMessage().getContent();

            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                question = matcher.group(1).trim();
                answer = matcher.group(2).trim();
            } else {
                question = null;
                answer = null;
            }

            attempt++;
        }

        if (question == null || answer == null || !isValidAnswer(answer)) {
            throw new RuntimeException("문제 생성 실패");
        }

        LocalDate today = LocalDate.now();
        int lastQuestionNo = questionRepository.findMaxQuestionNoByDate(today);
        int newQuestionNo = lastQuestionNo + 1;

        Question question1 = questionRepository.save(Question.builder()
                .content(question)
                .answer(answer)
                .date(today)
                .questionNo(newQuestionNo)
                .build());

        return question1;
    }

    private boolean isValidAnswer(String answer) {
        if (answer == null) {
            return false;
        }
        if (answer.length() > 20) {
            return false;
        }
        if (answer.contains("```")) {
            return false;
        }
        return true;
    }

    private String modifyPrompt(String prompt, String question, String answer) {
        StringBuilder modifiedPrompt = new StringBuilder(prompt);

        // 답변의 형식이 잘못됬을 경우
        if (question == null || answer == null) {
            modifiedPrompt.append("\n\n주의: 답변은 '문제: ... 답: ...' 형식으로 작성해 주세요.");
        }

        // 답변 길이가 50자를 넘는 경우
        if (answer != null && answer.length() > 50) {
            modifiedPrompt.append("\n\n답변의 길이는 50자를 넘지 않도록 해주세요.");
        }

        // 답변에 코드 블럭이 포함된 경우
        if (answer != null && answer.contains("```")) {
            modifiedPrompt.append("\n\n답변에 코드 블럭(```)을 포함하지 말아 주세요.");
        }

        return modifiedPrompt.toString();
    }


}
