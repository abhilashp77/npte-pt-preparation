package com.npte.portal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageGenerationResponse {
    private String base64Image;
}
