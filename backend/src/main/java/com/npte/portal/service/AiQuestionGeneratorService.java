package com.npte.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.npte.portal.domain.Answer;
import com.npte.portal.domain.Explanation;
import com.npte.portal.domain.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiQuestionGeneratorService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${anthropic.api.key}")
    private String anthropicApiKey;

    private static final String PROMPT = """
        You are an expert physical therapy professor writing a question for the NPTE (National Physical Therapy Examination).
        
        Create a challenging, high-quality, clinical scenario-based multiple choice question.
        The question must have exactly 4 options (A, B, C, D).
        Provide a detailed clinical explanation of why the answer is correct, and specifically why the others are incorrect.
        Provide 1-2 authoritative medical/PT textbook references.
        
        Return ONLY a valid JSON object matching this exact schema, with no markdown formatting or extra text:
        {
           "bodySystem": "Musculoskeletal OR Neuromuscular OR Cardiopulmonary",
           "difficulty": "Hard",
           "questionText": "The actual clinical scenario text...",
           "correctAnswer": "A",
           "options": {
               "A": "Option A text",
               "B": "Option B text",
               "C": "Option C text",
               "D": "Option D text"
           },
           "explanation": "Detailed rationale here...",
           "references": ["Textbook 1", "Textbook 2"]
        }
        """;

    public AiQuestionGeneratorService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public Question generateNewQuestion() {
        String jsonResponse = null;

        // Try OpenAI first
        try {
            log.info("Attempting question generation with OpenAI...");
            jsonResponse = chatClient.prompt()
                    .user(PROMPT)
                    .call()
                    .content();
            log.info("OpenAI generation successful.");
        } catch (Exception openAiEx) {
            log.warn("OpenAI failed: {}. Falling back to Gemini...", openAiEx.getMessage());
        }

        // Fallback to Gemini
        if (jsonResponse == null) {
            try {
                jsonResponse = callGemini();
            } catch (Exception geminiEx) {
                log.warn("Gemini failed: {}. Falling back to Anthropic...", geminiEx.getMessage());
            }
        }

        // Fallback to Anthropic
        if (jsonResponse == null) {
            jsonResponse = callAnthropic();
        }

        return parseJsonToQuestion(jsonResponse);
    }

    private String callGemini() {
        log.info("Attempting question generation with Google Gemini...");

        String[] models = {"gemini-1.5-flash", "gemini-2.0-flash", "gemini-pro"};

        for (String model : models) {
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + geminiApiKey;

                    Map<String, Object> requestBody = Map.of(
                        "contents", List.of(
                            Map.of("parts", List.of(
                                Map.of("text", PROMPT)
                            ))
                        ),
                        "generationConfig", Map.of(
                            "temperature", 0.7,
                            "maxOutputTokens", 2048
                        )
                    );

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
                    Map body = response.getBody();

                    List<Map> candidates = (List<Map>) body.get("candidates");
                    Map content = (Map) candidates.get(0).get("content");
                    List<Map> parts = (List<Map>) content.get("parts");
                    String text = (String) parts.get(0).get("text");

                    log.info("Gemini generation successful with model: {}", model);
                    return text;
                } catch (Exception e) {
                    log.warn("Gemini attempt {}/{} with model {} failed: {}", attempt, 3, model, e.getMessage());
                    if (attempt < 3) {
                        try { Thread.sleep(attempt * 2000L); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    }
                }
            }
        }

        throw new RuntimeException("All Gemini models failed after retries.");
    }

    private String callAnthropic() {
        log.info("Attempting question generation with Anthropic Claude...");
        String url = "https://api.anthropic.com/v1/messages";

        String requestJson;
        try {
            Map<String, Object> requestBody = Map.of(
                "model", "claude-3-5-haiku-latest",
                "max_tokens", 2048,
                "messages", List.of(
                    Map.of("role", "user", "content", PROMPT)
                )
            );
            requestJson = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Anthropic request", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", anthropicApiKey);
        headers.set("anthropic-version", "2023-06-01");
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map body = response.getBody();

            // Extract text from Anthropic response: content[0].text
            List<Map> content = (List<Map>) body.get("content");
            String text = (String) content.get(0).get("text");

            log.info("Anthropic generation successful.");
            return text;
        } catch (Exception e) {
            throw new RuntimeException("All providers (OpenAI, Gemini, Anthropic) failed. Anthropic error: " + e.getMessage(), e);
        }
    }

    private Question parseJsonToQuestion(String jsonResponse) {
        // Strip markdown backticks if the LLM accidentally includes them
        if (jsonResponse.startsWith("```json")) {
            jsonResponse = jsonResponse.substring(7);
        }
        if (jsonResponse.startsWith("```")) {
            jsonResponse = jsonResponse.substring(3);
        }
        if (jsonResponse.endsWith("```")) {
            jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 3);
        }
        jsonResponse = jsonResponse.trim();

        try {
            Map<String, Object> map = objectMapper.readValue(jsonResponse, Map.class);

            Question q = new Question();
            q.setBodySystem((String) map.get("bodySystem"));
            q.setDifficulty((String) map.get("difficulty"));
            q.setQuestionText((String) map.get("questionText"));

            Map<String, String> opts = (Map<String, String>) map.get("options");
            Answer a = new Answer();
            a.setOptionA(opts.get("A"));
            a.setOptionB(opts.get("B"));
            a.setOptionC(opts.get("C"));
            a.setOptionD(opts.get("D"));
            a.setCorrectAnswer((String) map.get("correctAnswer"));
            a.setQuestion(q);
            q.setAnswer(a);

            Explanation e = new Explanation();
            e.setExplanationText((String) map.get("explanation"));
            List<String> refs = (List<String>) map.get("references");
            e.setReferences(refs);
            e.setQuestion(q);
            q.setExplanation(e);

            return q;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI question response: " + e.getMessage() + "\nRaw: " + jsonResponse, e);
        }
    }
}
