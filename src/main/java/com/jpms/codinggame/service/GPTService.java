package com.jpms.codinggame.service;

import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.entity.QuestionType;
import com.jpms.codinggame.global.dto.GPTRequestDto;
import com.jpms.codinggame.global.dto.GPTResponseDto;
import com.jpms.codinggame.repository.question.QuestionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class GPTService {

    private final QuestionRepository questionRepository;

    public String createPrompt(QuestionType qType){
        String prompt = qType + " 코딩 문제를 생성해 주세요.\n" +
                "문제는 컴파일 시 결과를 물어보거나 코드 중 빈칸을 채우는 형식이어야 합니다.\n" +
                "5가지의 보기가 주어지고 그 중 답을 고르는 문제입니다.\n" +
                "다음 형식을 따라 작성해 주세요:\n" +
                "문제: [여기에 문제를 적어 주세요]\n" +
                "보기:\n" +
                "1. [보기1]\n" +
                "2. [보기2]\n" +
                "3. [보기3]\n" +
                "4. [보기4]\n" +
                "5. [보기5]\n" +
                "답: [여기에 한자리 숫자로 된 답을 적어 주세요]";
        return prompt;
    }
    public Question createQuestion(String model, String apiURL, RestTemplate template) {
        String question = null;
        String answer = null;
        String choice = null;
        QuestionType qType = getRandomEnumValue(QuestionType.class);
        Pattern pattern = Pattern.compile("문제:(.*)보기:(.*)답:(.*)", Pattern.DOTALL);

        //시도 횟수
        int attempt = 0;
        final int maxAttempts = 5;

        while ((question == null || answer == null || !isValidAnswer(answer)) && attempt < maxAttempts) {

            String prompt = createPrompt(qType);

            if (attempt > 0) {
                prompt = modifyPrompt(prompt, question, choice, answer);
            }

            int maxTokens = 2000;
            GPTRequestDto request = new GPTRequestDto(model, prompt, maxTokens);
            GPTResponseDto chatGPTResponse = template.postForObject(apiURL, request, GPTResponseDto.class);
            String content = chatGPTResponse.getChoices().get(0).getMessage().getContent();

            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                question = matcher.group(1).trim();
                choice = matcher.group(2).trim();
                answer = matcher.group(3).trim();
            } else {
                question = null;
                answer = null;
            }

            attempt++;
        }

        if (question == null || answer == null || !isValidAnswer(answer) || !isValidChoices(choice)) {
            throw new RuntimeException("문제 생성 실패");
        }

        LocalDate today = LocalDate.now();
        int lastQuestionNo = questionRepository.findMaxQuestionNoByDate(today);
        int newQuestionNo = lastQuestionNo + 1;

        Question question1 = questionRepository.save(Question.builder()
                .content(question)
                .answer(answer)
                .choice(choice)
                .date(today)
                .questionType(qType)
                .questionNo(newQuestionNo)
                .build());

        return question1;
    }

    public static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass) {
        Random random = new Random();
        T[] enumValues = enumClass.getEnumConstants();
        int randomIndex = random.nextInt(enumValues.length);
        return enumValues[randomIndex];
    }

    // 정답 확인 로직
    private boolean isValidAnswer(String answer) {
        if (answer == null) {
            return false;
        }
        if (answer.length() != 1) {
            return false;
        }
        if (!answer.matches("[1-5]")) {
            return false;
        }
        return true;
    }

    // 보기 확인 로직
    private boolean isValidChoices(String choices) {
        if (choices == null) {
            return false;
        }
        String[] options = choices.split("\n");
        if (options.length != 5) {
            return false;
        }
        for (String option : options) {
            if (!option.matches("\\d\\.\\s.*")) {
                return false;
            }
        }
        return true;
    }

    private String modifyPrompt(String prompt, String question, String choices, String answer) {
        StringBuilder modifiedPrompt = new StringBuilder(prompt);

        if (question == null) {
            modifiedPrompt.append("\n\n주의: 문제를 명확하게 작성해 주세요.");
        }
        if (choices == null || !isValidChoices(choices)) {
            modifiedPrompt.append("\n\n주의: 보기 5가지를 다음 형식으로 작성해 주세요:\n" +
                    "1. [보기1]\n" +
                    "2. [보기2]\n" +
                    "3. [보기3]\n" +
                    "4. [보기4]\n" +
                    "5. [보기5]");
        }
        if (answer == null || !isValidAnswer(answer)) {
            modifiedPrompt.append("\n\n주의: 답을 단일 숫자로 작성해 주세요 (1-5 중 하나).");
        }

        return modifiedPrompt.toString();
    }



}
