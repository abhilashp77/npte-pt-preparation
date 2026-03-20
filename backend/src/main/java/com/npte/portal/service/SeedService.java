package com.npte.portal.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.npte.portal.domain.Answer;
import com.npte.portal.domain.Explanation;
import com.npte.portal.domain.Question;
import com.npte.portal.domain.Topic;
import com.npte.portal.repository.QuestionRepository;
import com.npte.portal.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Transactional
    public void performFreshSeed() throws Exception {
        log.info("API Triggered: Clearing and Reseeding Database...");

        // 1. Clear everything
        topicRepository.truncateAndResetTopics();
        questionRepository.truncateAndResetQuestions();

        // 2. Seed Topics
        seedTopics();

        // 3. Seed Questions
        seedQuestions();

        log.info("Database Refresh Successful.");
    }

    private void seedTopics() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:topics-seed.json");
        if (resource.exists()) {
            InputStream is = resource.getInputStream();
            List<Topic> topics = objectMapper.readValue(is, new TypeReference<>() {
            });
            topicRepository.saveAll(topics);
        }
    }

    private void seedQuestions() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:questions-seed.json");
        if (!resource.exists())
            throw new RuntimeException("questions-seed.json not found!");

        InputStream is = resource.getInputStream();
        List<Map<String, Object>> raw = objectMapper.readValue(is, new TypeReference<>() {
        });
        List<Question> questions = new ArrayList<>();

        for (Map<String, Object> map : raw) {
            Question q = Question.builder()
                    .questionText((String) map.get("questionText"))
                    .bodySystem((String) map.get("bodySystem"))
                    .difficulty((String) map.get("difficulty"))
                    .build();

            Map<String, String> opts = (Map<String, String>) map.get("options");
            Answer a = Answer.builder()
                    .optionA(opts.get("A")).optionB(opts.get("B"))
                    .optionC(opts.get("C")).optionD(opts.get("D"))
                    .correctAnswer((String) map.get("correctAnswer"))
                    .question(q).build();
            q.setAnswer(a);

            Explanation e = Explanation.builder()
                    .explanationText((String) map.get("explanation"))
                    .references((List<String>) map.get("references"))
                    .question(q).build();
            q.setExplanation(e);

            questions.add(q);
        }
        questionRepository.saveAll(questions);
    }
}
