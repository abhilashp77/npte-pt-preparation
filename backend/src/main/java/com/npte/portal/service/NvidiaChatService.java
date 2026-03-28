package com.npte.portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.npte.portal.dto.FollowUpAnswerResponse;
import com.npte.portal.dto.FollowUpOptionsResponse;
import com.npte.portal.dto.HintResponse;
import com.npte.portal.dto.ImageGenerationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class NvidiaChatService {

    @Value("${nvidia.api.key:local-dummy-key}")
    private String apiKey;

    @Value("${nvidia.sd3.key:local-dummy-key}")
    private String sd3ApiKey;

    @Value("${nvidia.sd3.url:https://ai.api.nvidia.com/v1/genai/stabilityai/stable-diffusion-3-medium}")
    private String sd3ApiUrl;

    @Value("${nvidia.api.url:https://integrate.api.nvidia.com/v1}")
    private String apiUrl;

    @Value("${nvidia.api.model:meta/llama-3.1-8b-instruct}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate 3 follow-up question suggestions based on the Q&A context.
     */
    public FollowUpOptionsResponse generateFollowUpOptions(String question, String correctAnswer, String explanation) {
        String systemPrompt = """
            You are an NPTE (National Physical Therapy Examination) study assistant.
            Based on the question, correct answer, and explanation provided, generate exactly 3 concise follow-up questions
            that a PT student might want to ask to deepen their understanding.
            
            Rules:
            - Each question should be on its own line
            - Number them 1. 2. 3.
            - Keep each question under 15 words
            - Make them clinically relevant and progressively deeper
            - Do NOT include any other text, preamble, or explanation
            """;

        String userMessage = String.format("""
            Question: %s
            Correct Answer: %s
            Explanation: %s
            
            Generate 3 follow-up questions:""", question, correctAnswer, explanation);

        try {
            String response = callNvidiaApi(systemPrompt, userMessage);
            List<String> options = parseFollowUpOptions(response);
            return FollowUpOptionsResponse.builder().options(options).build();
        } catch (Exception e) {
            log.error("Failed to generate follow-up options from NVIDIA NIM: {}", e.getMessage());
            // Fallback: generate generic follow-up questions
            return FollowUpOptionsResponse.builder()
                    .options(List.of(
                            "Can you explain the underlying anatomy involved?",
                            "What are the key differential diagnoses to consider?",
                            "What evidence-based interventions are most effective?"
                    ))
                    .build();
        }
    }

    /**
     * Generate an answer for a specific follow-up question.
     */
    public FollowUpAnswerResponse answerFollowUpQuestion(String originalQuestion, String correctAnswer,
                                                          String explanation, String followUpQuestion) {
        String systemPrompt = """
            You are an NPTE (National Physical Therapy Examination) study assistant.
            A student just answered a practice question and is now asking a follow-up question based on the explanation.
            Provide a clear, concise, and clinically accurate answer.
            
            Rules:
            - Keep the answer focused and under 200 words
            - Use bullet points where helpful
            - Reference relevant clinical concepts
            - Be educational and supportive in tone
            """;

        String userMessage = String.format("""
            Original Question: %s
            Correct Answer: %s
            Explanation: %s
            
            Follow-up Question: %s
            
            Please answer the follow-up question:""", originalQuestion, correctAnswer, explanation, followUpQuestion);

        try {
            String answer = callNvidiaApi(systemPrompt, userMessage);
            return FollowUpAnswerResponse.builder()
                    .question(followUpQuestion)
                    .answer(answer)
                    .build();
        } catch (Exception e) {
            log.error("Failed to answer follow-up from NVIDIA NIM: {}", e.getMessage());
            return FollowUpAnswerResponse.builder()
                    .question(followUpQuestion)
                    .answer("Unable to generate an answer at this time. Please try again later or consult your study materials.")
                    .build();
        }
    }

    /**
     * Generate a hint for a given question.
     */
    public HintResponse generateHint(String question) {
        String systemPrompt = """
            You are an NPTE (National Physical Therapy Examination) study assistant.
            A student is stuck on a theory/clinical application question and asked for a hint.
            Provide a concise, helpful hint that guides them toward the right thinking process without giving away the exact answer.
            
            Rules:
            - Keep the hint under 30 words
            - Do NOT state the answer directly
            - Point their attention to the key symptom, system, or differential fact mentioned in the question
            """;

        String userMessage = String.format("Question: %s\n\nPlease provide a short hint for this question:", question);

        try {
            String answer = callNvidiaApi(systemPrompt, userMessage);
            return HintResponse.builder()
                    .hint(answer)
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate hint from NVIDIA NIM: {}", e.getMessage());
            return HintResponse.builder()
                    .hint("Consider reviewing the core clinical concept or primary body system mentioned in the scenario.")
                    .build();
        }
    }

    private String callNvidiaApi(String systemPrompt, String userMessage) {
        String url = apiUrl + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/json");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        ));
        body.put("max_tokens", 512);
        body.put("temperature", 0.7);
        body.put("top_p", 0.7);
        body.put("stream", false);

        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("NVIDIA NIM API call failed: {}", e.getMessage());
            throw new RuntimeException("Failed to call NVIDIA NIM API", e);
        }
    }

    private List<String> parseFollowUpOptions(String response) {
        List<String> options = new ArrayList<>();
        String[] lines = response.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            // Remove numbering like "1. ", "2. ", "3. ", "1) ", etc.
            String cleaned = trimmed.replaceFirst("^\\d+[.)\\-]\\s*", "");
            if (!cleaned.isEmpty()) {
                options.add(cleaned);
            }
        }

        // Ensure we return exactly 3 options
        if (options.size() > 3) {
            options = options.subList(0, 3);
        }
        while (options.size() < 3) {
            options.add("Tell me more about this topic.");
        }

        return options;
    }

    public ImageGenerationResponse generateMedicalImage(String question, String correctAnswer, String explanation) {
        String systemPrompt = """
            You are an expert medical illustrator and prompt engineer.
            Given the medical case question, correct answer, and explanation, write a highly detailed, 
            descriptive visual prompt for an AI image generator (like Stable Diffusion) to create an accurate 
            medical illustration of the condition, anatomy, or procedure being discussed.
            
            STYLE GUIDELINES:
            - **Medical Precision**: Use the exact medical terms and anatomical structures mentioned in the question and explanation (e.g., "supraspinatus tendon", "L4-L5 intervertebral disc").
            - **Functional Anatomy**: Visually demonstrate the specific clinical condition or biomechanical position described (e.g., "Trendelenburg gait", "scapular winging").
            - **Style**: High-clarity 2D medical clinical diagram, professional anatomical drawing, or vector illustration.
            - **Clarity**: Ensure high contrast, sharp lines, and a pure white background for maximum educational value.
            - **Avoid**: 3D glossy renders, photorealistic stock-photos, blurry backgrounds, or generic "health" imagery.
            - **Educational Focus**: The image should look like it belongs in a medical textbook or clinical manual.
            
            Return ONLY the visual prompt text, without any conversational filler.
            """;

        String userMessage = String.format("Question: %s\nCorrect Answer: %s\nExplanation: %s", question, correctAnswer, explanation);

        String imagePrompt = "A high-clarity 2D medical clinical diagram showing proper patient positioning, white background.";
        try {
            imagePrompt = callNvidiaApi(systemPrompt, userMessage);
            if (imagePrompt == null || imagePrompt.trim().length() < 5) {
                imagePrompt = "A high-clarity 2D medical clinical diagram showing proper patient positioning, white background.";
            }
        } catch(Exception e) {
            log.error("Failed to extract prompt from LLM, defaulting.", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + sd3ApiKey);
        headers.set("Accept", "application/json");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("prompt", imagePrompt);
        body.put("negative_prompt", "3d render, glossy, photography, photorealistic, real person, face, blurry, low resolution, messy background, text, watermark, signature, distorted anatomy, unrealistic proportions");
        body.put("cfg_scale", 7);
        body.put("aspect_ratio", "1:1");
        body.put("seed", 0);
        body.put("steps", 50);

        String base64Image = null;
        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(sd3ApiUrl, HttpMethod.POST, entity, Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            if (responseBody != null) {
                if (responseBody.containsKey("artifacts")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> artifacts = (List<Map<String, Object>>) responseBody.get("artifacts");
                    if (!artifacts.isEmpty()) {
                        base64Image = (String) artifacts.get(0).get("base64");
                    }
                } else if (responseBody.containsKey("image")) {
                    base64Image = (String) responseBody.get("image");
                }
            }
        } catch(Exception e) {
            log.error("Stable Diffusion 3 API failure", e);
        }

        if (base64Image == null) {
            throw new RuntimeException("Failed to generate medical illustration. Please check NVIDIA SD3 API Key.");
        }

        return ImageGenerationResponse.builder()
                .base64Image(base64Image)
                .build();
    }
}

