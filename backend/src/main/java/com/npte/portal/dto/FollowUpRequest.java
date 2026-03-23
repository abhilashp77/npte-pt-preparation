package com.npte.portal.dto;

import lombok.Data;

@Data
public class FollowUpRequest {
    private String question;
    private String selectedAnswer;
    private String correctAnswer;
    private String explanation;
}
