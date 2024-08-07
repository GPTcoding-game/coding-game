package com.jpms.codinggame.service;

import com.jpms.codinggame.Oauth2.PrincipalDetails;
import com.jpms.codinggame.dto.QnaCreateRequestDto;
import com.jpms.codinggame.dto.QnaModifyRequestDto;
import com.jpms.codinggame.dto.QnaResDto;
import com.jpms.codinggame.entity.Qna;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.repository.QnaRepository;
import com.jpms.codinggame.repository.question.QuestionRepository;
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
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        qnaRepository.save(Qna
                        .builder()
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .question(questionRepository.findById(questionId).orElseThrow(RuntimeException::new))
                        .user(user)
                        .time(LocalDate.now())
                        .build());
    }


    //수정
    public void modifyQna(
            Long qnaId,
            Long questionId,
            QnaModifyRequestDto dto,
            Authentication authentication){
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(()->new CustomException(ErrorCode.INVALID_QNA_ID));

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        qnaRepository.save(Qna
                .builder()
                .id(qna.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .question(questionRepository.findById(questionId).orElseThrow(()->new CustomException(ErrorCode.INVALID_QUESTION_ID)))
                .user(user)
                .time(LocalDate.now())
                .commentList(qna.getCommentList())
                .build());
    };


    //삭제
    public void deleteQna(Long qnaId, Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        Qna qna = qnaRepository.findById(qnaId).orElseThrow(()->new CustomException(ErrorCode.INVALID_QNA_ID));

        if(qna.getUser() != user) throw new CustomException(ErrorCode.GRANT_EXCEPTION);
        qnaRepository.deleteById(qnaId);
    };


    //질문 전체 가져오기
    //page 형태로 변경하기
    public List<QnaResDto> getQnaList(Long questionId){
        //Question 존재 유무
        questionRepository.findById(questionId).orElseThrow(()->new CustomException(ErrorCode.INVALID_QUESTION_ID));

        //qnaList 생성
        List<Qna> qnaList = qnaRepository.findAllByQuestionId(questionId);
        return qnaList
                .stream()
                .map(qna -> QnaResDto
                        .builder()
                        .qnaId(qna.getId())
                        .title(qna.getTitle())
                        .content(qna.getContent())
                        .time(LocalDate.now())
                        .nickname(qna.getUser().getNickName())
                        .commentVolume(qna.getCommentList().size())
                        .build())
                .collect(Collectors.toList());
    }

    // 특정 유저의 질문 가져오기
    //위의 getQnaList의 페이징 형식을 그대로 따라 수정필요
    public List<QnaResDto> getMyQna(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        List<Qna> myQna = qnaRepository.findAllByUser(user);
        return  myQna
                .stream()
                .map(qna -> QnaResDto
                        .builder()
                        .title(qna.getTitle())
                        .content(qna.getContent())
                        .time(LocalDate.now())
                        .nickname(qna.getUser().getNickName())
                        .commentVolume(qna.getCommentList().size())
                        .build())
                .collect(Collectors.toList());
    }

    //특정 질문 가져오기
    public QnaResDto getQna(Long qnaId){
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(()->new CustomException(ErrorCode.INVALID_QNA_ID));
        return QnaResDto.fromEntity(qna);
    }

    //최근 생성 된 질문 가져오기
    public List<QnaResDto> getRecent5Qna(){
        List<Qna> qnaList = qnaRepository.findTop5ByOrderByIdDesc();
        return qnaList
                .stream()
                .map(qna -> QnaResDto
                        .builder()
                        .qnaId(qna.getId())
                        .title(qna.getTitle())
                        .nickname(qna.getUser().getNickName())
                        .content(qna.getContent())
                        .commentVolume(qna.getCommentList().size())
                        .time(qna.getTime())
                        .build())
                .toList();
    }

}
