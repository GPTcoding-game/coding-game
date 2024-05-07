package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.QnaCreateRequestDto;
import com.jpms.codinggame.entity.Qna;
import com.jpms.codinggame.repository.QnaRepository;
import com.jpms.codinggame.repository.QuestionRepository;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaService {
    private final QnaRepository qnaRepository;
    private final QuestionRepository questionRepository;

    //생성
    public void createQna(Long questionId,QnaCreateRequestDto dto){
        //Question 존재 유무 판별
        questionRepository.findById(questionId).orElseThrow(RuntimeException::new);


        qnaRepository.save(Qna
                        .builder()
                        .title(dto.getTitle())
                        .content(dto.getContent())
//                        .question()
//                        .user()
                        .time(dto.getTime())
                        .build());
    }
    //수정
    //삭제
}
