package com.npte.portal.dto;

import lombok.Data;

@Data
public class FollowUpAnswerRequest {
    private String originalQuestion;
    private String correctAnswer;
    private String explanation;
    private String followUpQuestion;
}
