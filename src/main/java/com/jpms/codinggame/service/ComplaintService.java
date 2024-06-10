package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.ComplaintReqDto;
import com.jpms.codinggame.dto.ComplaintResDto;
import com.jpms.codinggame.entity.Complaint;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.ComplaintRepository;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComplaintService {
    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;

    /*
    * 신고 요청
    * */
    public void createComplaint(
            ComplaintReqDto complaintReqDto,
            Authentication authentication){
        User user = userRepository
                .findById((Long) authentication.getPrincipal())
                .orElseThrow(RuntimeException::new);

        //Qna 신고 저장
        if(complaintReqDto.getCommentId() == null){
            complaintRepository.save(
                    Complaint
                            .builder()
                            .user(user)
                            .title(complaintReqDto.getTitle())
                            .content(complaintReqDto.getContent())
                            .quaId(complaintReqDto.getQnaId())
                            .build());
        }
        //Comment 신고 저장
        else{
            complaintRepository.save(
                    Complaint
                            .builder()
                            .user(user)
                            .title(complaintReqDto.getTitle())
                            .content(complaintReqDto.getContent())
                            .commentId(complaintReqDto.getCommentId())
                            .build());
        }
    }

    /*
    * 신고 목록 가져오기
    * */
    public List<ComplaintResDto> getComplaintAll(){
        List<Complaint> complaintList = complaintRepository.findAll();
        return complaintList
                .stream()
                .map(complaint -> ComplaintResDto
                        .builder()
                        .complaintId(complaint.getId())
                        .userName(complaint.getUser().getUserName())
                        .title(complaint.getTitle())
                        .content(complaint.getContent())
                        .qnaId(complaint.getId())
                        .commentId(complaint.getCommentId())
                        .build())
                .toList();
    }
}
