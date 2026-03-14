package com.npte.portal.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SubmitAnswerResponse {
    private String correctAnswer;
    private String explanation;
    private List<String> references;
    private List<String> images;
}
