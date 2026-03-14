package com.npte.portal.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuestionDto {
    private Long id;
    private String question;
    private List<String> options;
    private String difficulty;
    private String system;
}
