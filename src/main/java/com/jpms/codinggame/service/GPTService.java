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
                "보기: [여기에 숫자로 된 보기 5가지를 적어 주세요]\n" +
                "답: [여기에 한자리 숫자로 된 답을 적어 주세요]";
        return prompt;
    }
    public Question createQuestion(String model, String apiURL, RestTemplate template) {
        String question = null;
        String answer = null;
        String choice = null;
        String content = null;
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
            content = chatGPTResponse.getChoices().get(0).getMessage().getContent();

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

        if (question == null || answer == null || !isValidAnswer(answer)) {
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

    private boolean isValidAnswer(String answer) {
        if (answer == null) {
            return false;
        }
        if (answer.length() > 1) {
            return false;
        }
        if (answer.contains("```")) {
            return false;
        }
        return true;
    }

    private String modifyPrompt(String prompt, String question, String choices, String answer) {
        StringBuilder modifiedPrompt = new StringBuilder(prompt);

        // 답변의 형식이 잘못됬을 경우
        if (question == null || choices == null ||answer == null) {
            modifiedPrompt.append("\n\n주의: 답변은 '문제: ... 보기: ... 답: ...' 형식으로 작성해 주세요.");
        }

        // 답변 길이가 50자를 넘는 경우
        if (answer != null && answer.length() > 1) {
            modifiedPrompt.append("\n\n주의: 단일 숫자로 답을 표시 해주세요.");
        }

        // 답변에 코드 블럭이 포함된 경우
        if (answer != null && answer.contains("```")) {
            modifiedPrompt.append("\n\n주의: 답변에 코드 블럭(```)을 포함하지 말아 주세요.");
        }

        return modifiedPrompt.toString();
    }



}
