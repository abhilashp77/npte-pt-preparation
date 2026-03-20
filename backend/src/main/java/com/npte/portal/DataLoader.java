package com.npte.portal;

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
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

        private final QuestionRepository questionRepository;
        private final TopicRepository topicRepository;
        private final ObjectMapper objectMapper;
        private final ResourceLoader resourceLoader;

        @Override
        @Transactional
        public void run(String... args) {
                log.info("--- 🔄 Database Refresh Started ---");

                // 1. Refresh Topics
                refreshTopicData();

                // 2. Refresh Questions
                refreshBulkData();

                log.info("--- ✅ Database Refresh Complete ---");
        }

        private void refreshTopicData() {
                try {
                        Resource resource = resourceLoader.getResource("classpath:topics-seed.json");
                        if (!resource.exists()) {
                                log.warn("⚠️ topics-seed.json not found. Skipping topic seeding.");
                                return;
                        }

                        log.info("Cleaning Topic table...");
                        topicRepository.deleteAll();

                        InputStream inputStream = resource.getInputStream();
                        List<Topic> topics = objectMapper.readValue(inputStream, new TypeReference<>() {
                        });

                        topicRepository.saveAll(topics);
                        log.info("Successfully inserted {} fresh topics.", topics.size());
                } catch (Exception e) {
                        log.error("❌ Failed to load topic seed data: {}", e.getMessage());
                }
        }

        private void refreshBulkData() {
                try {
                        Resource resource = resourceLoader.getResource("classpath:questions-seed.json");
                        if (!resource.exists()) {
                                log.warn("⚠️ questions-seed.json not found. Falling back to sample data.");
                                loadSampleData();
                                return;
                        }

                        log.info("Cleaning Question, Answer, and Explanation tables...");
                        questionRepository.deleteAll();

                        InputStream inputStream = resource.getInputStream();
                        List<Map<String, Object>> rawQuestions = objectMapper.readValue(inputStream,
                                        new TypeReference<>() {
                                        });

                        List<Question> questionsToSave = new ArrayList<>();

                        for (Map<String, Object> map : rawQuestions) {
                                // Build Question
                                Question q = Question.builder()
                                                .questionText((String) map.get("questionText"))
                                                .bodySystem((String) map.get("bodySystem"))
                                                .difficulty((String) map.get("difficulty"))
                                                .build();

                                // Build Answer & Link to Question
                                Map<String, String> opts = (Map<String, String>) map.get("options");
                                Answer a = Answer.builder()
                                                .optionA(opts.get("A"))
                                                .optionB(opts.get("B"))
                                                .optionC(opts.get("C"))
                                                .optionD(opts.get("D"))
                                                .correctAnswer((String) map.get("correctAnswer"))
                                                .question(q) // Link
                                                .build();
                                q.setAnswer(a);

                                // Build Explanation & Link to Question
                                Explanation e = Explanation.builder()
                                                .explanationText((String) map.get("explanation"))
                                                .references((List<String>) map.get("references"))
                                                .question(q) // Link
                                                .build();
                                q.setExplanation(e);

                                questionsToSave.add(q);
                        }

                        questionRepository.saveAll(questionsToSave);
                        log.info("Successfully inserted {} fresh questions.", questionsToSave.size());
                } catch (Exception e) {
                        log.error("❌ Failed to load bulk seed data: {}", e.getMessage(), e);
                        loadSampleData();
                }
        }

        private void loadSampleData() {
                log.info("Loading fallback sample data...");
                questionRepository.deleteAll();

                // Sample Question 1
                Question q1 = Question.builder()
                                .questionText("Which oxygen saturation level should the therapist maintain during exercise in COPD?")
                                .bodySystem("Cardiovascular & Pulmonary")
                                .difficulty("Exam Level")
                                .build();

                Answer a1 = Answer.builder()
                                .optionA("80%").optionB("85%").optionC("88%").optionD("96%")
                                .correctAnswer("C").question(q1).build();

                Explanation e1 = Explanation.builder()
                                .explanationText("Saturation should generally remain above 88%.")
                                .references(List.of("Source 1")).question(q1).build();

                q1.setAnswer(a1);
                q1.setExplanation(e1);

                // Sample Question 2 from README
                Question q2 = Question.builder()
                                .questionText("A patient presents with L4 nerve root compression. Which muscle is most likely weakened?")
                                .bodySystem("Neuromuscular & Nervous")
                                .difficulty("Exam Level")
                                .build();

                Answer a2 = Answer.builder()
                                .optionA("Tibialis anterior")
                                .optionB("Gastrocnemius")
                                .optionC("Gluteus maximus")
                                .optionD("Peroneus longus")
                                .correctAnswer("A")
                                .build();

                Explanation e2 = Explanation.builder()
                                .explanationText(
                                                "L4 myotome contributes significantly to ankle dorsiflexion via tibialis anterior.")
                                .references(List.of("https://www.physio-pedia.com/Lumbosacral_Radiculopathy"))
                                .build();

                q2.setAnswer(a2);
                q2.setExplanation(e2);

                // Sample Question 3
                Question q3 = Question.builder()
                                .questionText("A physical therapist evaluates a patient who presents with right-sided homonymous hemianopsia, right-sided hemiparesis, and expressive aphasia. Which artery is most likely occluded?")
                                .bodySystem("Neuromuscular & Nervous")
                                .difficulty("Exam Level")
                                .build();

                Answer a3 = Answer.builder()
                                .optionA("Left middle cerebral artery")
                                .optionB("Right middle cerebral artery")
                                .optionC("Left anterior cerebral artery")
                                .optionD("Right posterior cerebral artery")
                                .correctAnswer("A")
                                .build();

                Explanation e3 = Explanation.builder()
                                .explanationText(
                                                "A left middle cerebral artery (MCA) stroke typically presents with contralateral (right-sided) hemiparesis, contralateral hemianopsia, and aphasia (if the lesion is in the dominant hemisphere, which is typically the left).")
                                .references(List.of("https://www.physio-pedia.com/Middle_Cerebral_Artery_Stroke"))
                                .build();

                q3.setAnswer(a3);
                q3.setExplanation(e3);

                // Sample Question 4
                Question q4 = Question.builder()
                                .questionText("A patient has a deep partial-thickness burn over the anterior surface of the right arm and anterior right leg. Using the Rule of Nines, what is the estimated total body surface area (TBSA) involved?")
                                .bodySystem("Integumentary")
                                .difficulty("Moderate")
                                .build();

                Answer a4 = Answer.builder()
                                .optionA("9%")
                                .optionB("13.5%")
                                .optionC("18%")
                                .optionD("27%")
                                .correctAnswer("B")
                                .build();

                Explanation e4 = Explanation.builder()
                                .explanationText(
                                                "According to the Rule of Nines:\nAnterior right arm = 4.5%\nAnterior right leg = 9%\nTotal = 13.5%")
                                .references(List.of("https://www.physio-pedia.com/Burns"))
                                .build();

                q4.setAnswer(a4);
                q4.setExplanation(e4);

                // Sample Question 5
                Question q5 = Question.builder()
                                .questionText("During a gait analysis, a therapist observes a patient demonstrating a 'steppage' gait pattern during the swing phase. This abnormality is primarily caused by weakness in which muscle group?")
                                .bodySystem("Musculoskeletal")
                                .difficulty("Easy")
                                .build();

                Answer a5 = Answer.builder()
                                .optionA("Hip flexors")
                                .optionB("Plantarflexors")
                                .optionC("Knee extensors")
                                .optionD("Dorsiflexors")
                                .correctAnswer("D")
                                .build();

                Explanation e5 = Explanation.builder()
                                .explanationText(
                                                "A steppage gait is characterized by excessive hip and knee flexion during the swing phase to compensate for foot drop, which is caused by weakness of the ankle dorsiflexors (e.g., tibialis anterior).")
                                .references(List.of("https://www.physio-pedia.com/Gait_Analysis"))
                                .build();

                q5.setAnswer(a5);
                q5.setExplanation(e5);

                questionRepository.saveAll(List.of(q1, q2, q3, q4, q5));
                System.out.println("Sample NPTE questions loaded into database.");
        }
}
