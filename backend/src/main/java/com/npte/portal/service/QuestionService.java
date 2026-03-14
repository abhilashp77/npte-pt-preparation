package com.npte.portal.service;

import com.npte.portal.domain.Answer;
import com.npte.portal.domain.Image;
import com.npte.portal.domain.Question;
import com.npte.portal.dto.QuestionDto;
import com.npte.portal.dto.SubmitAnswerRequest;
import com.npte.portal.dto.SubmitAnswerResponse;
import com.npte.portal.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AiQuestionGeneratorService aiQuestionGeneratorService;

    public QuestionDto getRandomQuestion(Long excludeId) {
        Question question;
        boolean tryDatabase = true;

        if (excludeId != null && questionRepository.count() > 1) {
            question = questionRepository.findRandomQuestionExcludingId(excludeId).orElse(null);
            if (question == null) {
                // If the only other questions are somehow invalid, ignore excludeId
                question = questionRepository.findRandomQuestion().orElse(null);
            }
        } else {
            question = questionRepository.findRandomQuestion().orElse(null);
        }

        // If no questions exist, or we want to force AI generation occasionally, we generate one
        if (question == null) {
            log.info("No fresh questions found in database. Triggering AI Generation...");
            question = aiQuestionGeneratorService.generateNewQuestion();
            question = questionRepository.save(question); // Save it so we can reuse it later!
            log.info("Successfully generated and saved new question ID: {}", question.getId());
        }

        Answer answer = question.getAnswer();
        List<String> options = new ArrayList<>();
        options.add(answer.getOptionA());
        options.add(answer.getOptionB());
        options.add(answer.getOptionC());
        options.add(answer.getOptionD());

        return QuestionDto.builder()
                .id(question.getId())
                .question(question.getQuestionText())
                .options(options)
                .difficulty(question.getDifficulty())
                .system(question.getBodySystem())
                .build();
    }

    public QuestionDto forceGenerateAiQuestion() {
        log.info("Forcing explicit AI Question Generation via API...");
        Question question = aiQuestionGeneratorService.generateNewQuestion();
        question = questionRepository.save(question);
        log.info("Successfully generated and saved new AI question ID: {}", question.getId());

        Answer answer = question.getAnswer();
        List<String> options = new ArrayList<>();
        options.add(answer.getOptionA());
        options.add(answer.getOptionB());
        options.add(answer.getOptionC());
        options.add(answer.getOptionD());
        Collections.shuffle(options);

        return QuestionDto.builder()
                .id(question.getId())
                .question(question.getQuestionText())
                .options(options)
                .difficulty(question.getDifficulty())
                .system(question.getBodySystem())
                .build();
    }

    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        return SubmitAnswerResponse.builder()
                .correctAnswer(question.getAnswer().getCorrectAnswer())
                .explanation(question.getExplanation().getExplanationText())
                .references(question.getExplanation().getReferences())
                .images(question.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()))
                .build();
    }
}
