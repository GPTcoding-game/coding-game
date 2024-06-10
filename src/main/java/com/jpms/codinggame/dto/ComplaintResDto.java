package com.jpms.codinggame.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplaintResDto {
    private Long complaintId;
    private String userName;
    private String title;
    private String content;
    private Long qnaId;
    private Long commentId;
}
