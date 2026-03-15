package com.npte.portal.controller;

import com.npte.portal.dto.QuestionDto;
import com.npte.portal.dto.SubmitAnswerRequest;
import com.npte.portal.dto.SubmitAnswerResponse;
import com.npte.portal.dto.TopicDto;
import com.npte.portal.service.AiTopicService;
import com.npte.portal.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final AiTopicService aiTopicService;

    @GetMapping("/random")
    public QuestionDto getRandomQuestion(@RequestParam(required = false) Long excludeId) {
        return questionService.getRandomQuestion(excludeId);
    }

    @PostMapping("/submit")
    public SubmitAnswerResponse submitAnswer(@RequestBody SubmitAnswerRequest request) {
        return questionService.submitAnswer(request);
    }

    @GetMapping("/next")
    public QuestionDto getNextQuestion(@RequestParam(required = false) Long excludeId) {
        return questionService.getRandomQuestion(excludeId);
    }

    @GetMapping("/generate")
    public QuestionDto generateAiQuestion() {
        return questionService.forceGenerateAiQuestion();
    }

    @GetMapping("/topics/random")
    public TopicDto getRandomTopic() {
        return aiTopicService.generateRandomTopic();
    }
}
