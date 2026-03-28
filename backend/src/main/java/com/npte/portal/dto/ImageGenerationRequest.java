package com.npte.portal.dto;

import lombok.Data;

@Data
public class ImageGenerationRequest {
    private String question;
    private String correctAnswer;
    private String explanation;
}
