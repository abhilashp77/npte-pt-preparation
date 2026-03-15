package com.npte.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    private String title;
    private String category;
    private String content; // Markdown content
    private List<String> references;
    private List<String> imageUrls;
}
