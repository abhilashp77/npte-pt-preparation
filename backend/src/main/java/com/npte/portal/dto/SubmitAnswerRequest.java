package com.npte.portal.dto;

import lombok.Data;

@Data
public class SubmitAnswerRequest {
    private Long questionId;
    private String selectedAnswer;
}
