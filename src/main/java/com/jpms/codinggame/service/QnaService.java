package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.QnaCreateRequestDto;
import com.jpms.codinggame.dto.QnaModifyRequestDto;
import com.jpms.codinggame.dto.QnaResDto;
import com.jpms.codinggame.entity.Qna;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.QnaRepository;
import com.jpms.codinggame.repository.QuestionRepository;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaService {
    private final QnaRepository qnaRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    //생성
    public void createQna(
            Long questionId,
            QnaCreateRequestDto dto,
            Authentication authentication){
        qnaRepository.save(Qna
                        .builder()
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .question(questionRepository.findById(questionId).orElseThrow(RuntimeException::new))
                        .user(userRepository.findById((Long) authentication.getPrincipal()).orElseThrow(RuntimeException::new))
                        .time(LocalDate.now())
                        .build());
    }


    //수정
    public void modifyQna(
            Long qnaId,
            Long questionId,
            QnaModifyRequestDto dto,
            Authentication authentication){
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new);
        qnaRepository.save(Qna
                .builder()
                .id(qna.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .question(questionRepository.findById(questionId).orElseThrow(RuntimeException::new))
                .user(userRepository.findById((Long) authentication.getPrincipal()).orElseThrow(RuntimeException::new))
                .time(LocalDate.now())
                .build());
    };


    //삭제
    public void deleteQna(Long qnaId, Authentication authentication){
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new);
        User user = userRepository.findById((Long) authentication.getPrincipal()).orElseThrow(RuntimeException::new);
        if(qna.getUser() != user) throw new RuntimeException();
        qnaRepository.deleteById(qnaId);
    };


    //질문 전체 가져오기
    //page 형태로 변경하기
    public List<QnaResDto> getQnaList(Long questionId){
        //Question 존재 유무
        questionRepository.findById(questionId).orElseThrow(RuntimeException::new);

        //qnaList 생성
        List<Qna> qnaList = qnaRepository.findAllByQuestionId(questionId);
        return qnaList
                .stream()
                .map(qna -> QnaResDto
                        .builder()
                        .title(qna.getTitle())
                        .content(qna.getContent())
                        .time(LocalDate.now())
                        .nickname(qna.getUser().getNickName())
                        .build())
                .collect(Collectors.toList());
    }


    //특정 질문 가져오기
    public QnaResDto getQna(Long qnaId){
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new);
        return QnaResDto.fromEntity(qna);
    }
}
