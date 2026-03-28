package com.npte.portal.controller;

import com.npte.portal.dto.FollowUpAnswerRequest;
import com.npte.portal.dto.FollowUpAnswerResponse;
import com.npte.portal.dto.FollowUpOptionsResponse;
import com.npte.portal.dto.FollowUpRequest;
import com.npte.portal.dto.HintRequest;
import com.npte.portal.dto.HintResponse;
import com.npte.portal.service.NvidiaChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/followup")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FollowUpController {

    private final NvidiaChatService nvidiaChatService;

    @PostMapping("/options")
    public FollowUpOptionsResponse getFollowUpOptions(@RequestBody FollowUpRequest request) {
        return nvidiaChatService.generateFollowUpOptions(
                request.getQuestion(),
                request.getCorrectAnswer(),
                request.getExplanation()
        );
    }

    @PostMapping("/answer")
    public FollowUpAnswerResponse answerFollowUp(@RequestBody FollowUpAnswerRequest request) {
        return nvidiaChatService.answerFollowUpQuestion(
                request.getOriginalQuestion(),
                request.getCorrectAnswer(),
                request.getExplanation(),
                request.getFollowUpQuestion()
        );
    }

    @PostMapping("/hint")
    public HintResponse getHint(@RequestBody HintRequest request) {
        return nvidiaChatService.generateHint(request.getQuestion());
    }

    @PostMapping("/generate-image")
    public com.npte.portal.dto.ImageGenerationResponse generateImage(@RequestBody com.npte.portal.dto.ImageGenerationRequest request) {
        return nvidiaChatService.generateMedicalImage(
                request.getQuestion(),
                request.getCorrectAnswer(),
                request.getExplanation()
        );
    }
}
