package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.QnaCreateRequestDto;
import com.jpms.codinggame.dto.QnaModifyRequestDto;
import com.jpms.codinggame.dto.QnaResponseDto;
import com.jpms.codinggame.entity.Qna;
import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.repository.QnaRepository;
import com.jpms.codinggame.repository.QuestionRepository;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaService {
    private final QnaRepository qnaRepository;
    private final QuestionRepository questionRepository;

    //생성
    public void createQna(
            Long questionId,
            QnaCreateRequestDto dto){
        //todo Qna 객체에 작성자(username)이 있어야 할 것 같은데, 이거는 Authentication 객체 안에 있는 username 을 끌어와야 할 것 같다
        //일단 없이 진행하기
        qnaRepository.save(Qna
                        .builder()
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .question(questionRepository.findById(questionId).orElseThrow(RuntimeException::new))
//                        .user()
                        .time(dto.getTime())
                        .build());
    }


    //수정
    public void modifyQna(
            Long qnaId,
            Long questionId,
            QnaModifyRequestDto dto){
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new);
        qnaRepository.save(Qna
                .builder()
                .id(qna.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .question(questionRepository.findById(questionId).orElseThrow(RuntimeException::new))
//                .user()
                .time(dto.getTime())
                .build());
    };


    //삭제
    public void deleteQna(Long qnaId){
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new);
        qnaRepository.deleteById(qnaId);
    };


    //질문 전체 가져오기
    //page 형태로 변경하기
    public List<QnaResponseDto> getQnaList(Long questionId){
        //Question 존재 유무
        questionRepository.findById(questionId).orElseThrow(RuntimeException::new);

        //qnaList 생성
        List<Qna> qnaList = qnaRepository.findAllByQuestionId(questionId);
        return qnaList
                .stream()
                .map(qna -> QnaResponseDto
                        .builder()
                        .title(qna.getTitle())
                        .content(qna.getContent())
                        .time(qna.getTime())
                        .build())
                .collect(Collectors.toList());
    }


    //특정 질문 가져오기
    public QnaResponseDto getQna(Long qnaId){
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new);
        return QnaResponseDto.fromEntity(qna);
    }
}
