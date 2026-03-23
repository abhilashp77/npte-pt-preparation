package com.npte.portal.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FollowUpOptionsResponse {
    private List<String> options;
}
