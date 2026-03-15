package com.npte.portal.service;

import com.npte.portal.dto.TopicDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AiTopicService {

    private final com.npte.portal.repository.TopicRepository topicRepository;
    
    public AiTopicService(com.npte.portal.repository.TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public TopicDto generateRandomTopic() {
        // 1. Try to get from database first
        try {
            var topicOpt = topicRepository.findRandomTopic();
            if (topicOpt.isPresent()) {
                var topic = topicOpt.get();
                return TopicDto.builder()
                        .title(topic.getTitle())
                        .category(topic.getCategory())
                        .content(topic.getContent())
                        .references(topic.getReferences())
                        .imageUrls(topic.getImageUrls())
                        .build();
            }
        } catch (Exception e) {
            log.error("Failed to fetch topic from DB: {}", e.getMessage());
        }

        // 2. Since generation scripts are removed, directly fallback to hardcoded list
        log.info("No topic found in database, providing hardcoded fallback.");
        return getFallbackTopic();
    }

    private TopicDto getFallbackTopic() {
        List<TopicDto> fallbacks = List.of(
            TopicDto.builder()
                .title("Anterior Cruciate Ligament (ACL) Rehabilitation")
                .category("Musculoskeletal")
                .content("""
                    ### Introduction
                    The ACL is a vital stabilizer of the knee. Post-operative rehab follows a structured phase-based approach.
                    
                    ### Phases of Rehab
                    *   **Phase I**: Protection and Range of Motion (Week 1-4)
                    *   **Phase II**: Strengthening and Functional Tasks (Week 5-12)
                    *   **Phase III**: Return to Sport (Month 4+)
                    """)
                .references(List.of("Dutton's Orthopaedic Examination", "FSBPT Guidelines"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1530026405186-ed1f139313f8?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Chronic Obstructive Pulmonary Disease (COPD)")
                .category("Cardiopulmonary")
                .content("""
                    ### Overview
                    COPD is a chronic inflammatory lung disease that causes obstructed airflow from the lungs. Symptoms include breathing difficulty, cough, mucus production and wheezing.
                    
                    ### PT Management
                    - **Airway Clearance**: Haling, ACBT, and postural drainage.
                    - **Breathing Exercises**: Pursed-lip breathing and diaphragmatic breathing.
                    - **Exercise Training**: Interval training to improve aerobic capacity.
                    """)
                .references(List.of("O'Sullivan Physical Rehabilitation", "ACSM Guidelines"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1559757175-5700dde675bc?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Parkinson's Disease Gait Training")
                .category("Neuromuscular")
                .content("""
                    ### Clinical Presentation
                    Patients often present with bradykinesia, rigidity, and "freezing of gait" (FOG).
                    
                    ### Intervention Strategies
                    - **External Cueing**: Auditory (metronome) or visual (lines on floor) cues to bypass the basal ganglia.
                    - **Big and Loud**: Focus on large-amplitude movements (LSVT Big).
                    - **Balance Training**: Dynamic balance tasks to reduce fall risk.
                    """)
                .references(List.of("Umphred's Neurological Rehabilitation", "FSBPT Content Outline"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Stroke: Left Middle Cerebral Artery (MCA) Syndrome")
                .category("Neuromuscular")
                .content("""
                    ### Typical Presentation
                    - Right-sided hemiplegia and hemisensory loss (face/arm > leg).
                    - Aphasia (Global, Broca's, or Wernicke's).
                    - Right homonymous hemianopsia.
                    
                    ### PT Interventions
                    - **Early Mobilization**: Preventing contractures and pressure sores.
                    - **Constraint-Induced Movement Therapy**: Encouraging use of the affected limb.
                    - **Task-Specific Training**: Functional reaching, sit-to-stand, and gait.
                    """)
                .references(List.of("O'Sullivan Physical Rehabilitation", "Neurology for Physical Therapists"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Lymphedema Management (CDT)")
                .category("Other")
                .content("""
                    ### Complete Decongestive Therapy (CDT)
                    1. **Manual Lymphatic Drainage (MLD)**: Specialized massage to move fluid.
                    2. **Compression Bandaging**: Using short-stretch bandages.
                    3. **Remedial Exercise**: Active ROM to assist the muscle pump.
                    4. **Skin Care**: Preventing infection (cellulitis).
                    """)
                .references(List.of("Leduc & Leduc Lymphedema Care", "FSBPT Exam Blueprint"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1579684385127-1ef15d508118?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Spinal Cord Injury: Autonomic Dysreflexia")
                .category("Neuromuscular")
                .content("""
                    ### Medical Emergency
                    Occurs in patients with SCI at or above **T6**. triggered by a noxious stimulus below the level of the lesion (e.g., full bladder).
                    
                    ### Symptoms
                    - Sudden high BP, pounding headache.
                    - Flushing/sweating above the lesion.
                    - Bradycardia.
                    
                    ### Immediate PT Action
                    1. **Sit the patient up** immediately (to lower BP).
                    2. **Check the catheter/bladder** for blockage.
                    3. Seek medical assistance.
                    """)
                .references(List.of("ASIAN Standards for SCI", "Essentials of SCI Rehabilitation"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1516062423079-7ca13cdc7f5a?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Cardiac Rehab: Phase I (Inpatient)")
                .category("Cardiopulmonary")
                .content("""
                    ### Goals
                    - Patient education, early mobilization, and discharge planning.
                    - Monitoring hemodynamic response to low-level activity.
                    
                    ### Vital Sign Parameters
                    - **HR**: Should not exceed 20 bpm above resting (post-MI) or 30 bpm (post-surgery).
                    - **BP**: SBP should increase slightly; DBP should remain stable.
                    """)
                .references(List.of("Hillegass's Essentials of Cardiopulmonary PT", "AACVPR Guidelines"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Burn Care: Rule of Nines")
                .category("Other")
                .content("""
                    ### Surface Area Estimation
                    - **Head/Neck**: 9% | **Each Arm**: 9%
                    - **Each Leg**: 18% (9% anterior, 9% posterior)
                    - **Trunk**: 36% (18% anterior, 18% posterior)
                    - **Genitalia**: 1%
                    
                    ### PT Intervention
                    - **Positioning**: "The position of comfort is the position of contracture." (e.g., neck in extension, shoulder in abduction).
                    - **Scar Management**: Pressure garments and mobilization.
                    """)
                .references(List.of("Meyers' Integumentary Physical Therapy", "FSBPT Prep Guide"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1511174511562-5f7f18b874f8?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Pediatric Developmental Milestones (0-12 Months)")
                .category("Other")
                .content("""
                    ### Key Motor Milestones
                    - **4 Months**: Sits with support, rolls prone to supine.
                    - **6 Months**: Sits independently, rolls supine to prone.
                    - **9 Months**: Creeping (on hands and knees), pulls to stand.
                    - **12 Months**: Walking with one hand held or independently.
                    
                    ### PT Focus
                    Encouraging age-appropriate play and symmetry in movement.
                    """)
                .references(List.of("Tecklin's Pediatric Physical Therapy", "APTA Pediatrics section"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Rotator Cuff Repair: Post-Op Protocol")
                .category("Musculoskeletal")
                .content("""
                    ### Phase I: Protection (0-6 Weeks)
                    - Passive ROM only. No active movement of the shoulder.
                    - Sling used 24/7. Pendulum exercises can be started.
                    
                    ### Phase II: Active Motion (6-12 Weeks)
                    - Start AAROM and progress to AROM.
                    - Avoid heavy lifting or overhead reaching.
                    
                    ### Phase III: Strengthening (12+ Weeks)
                    - Progressive resistance exercises (PREs) with bands and weights.
                    """)
                .references(List.of("Manske's Postsurgical Rehabilitation", "Journal of Orthopaedic Sports PT"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1579126038827-77aa4759cc70?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Peripheral Arterial Disease (PAD): Exercise Therapy")
                .category("Cardiopulmonary")
                .content("""
                    ### Intermittent Claudication
                    Exercise is the gold standard for management. 
                    
                    ### Protocol
                    - **Interval Training**: Walk until pain is 3/4 on the claudication scale.
                    - **Rest**: Rest until pain is fully resolved before continuing.
                    - **Duration**: Goal of 30-50 minutes per session, 3x per week.
                    
                    ### Goals
                    Improve collateral circulation and metabolic efficiency of muscles.
                    """)
                .references(List.of("AHA/ACC Secondary Prevention Guidelines", "ACSM Exercise Guidelines"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&q=80&w=800"))
                .build(),
            TopicDto.builder()
                .title("Tens vs. NMES: Clinical Application")
                .category("Other")
                .content("""
                    ### TENS (Transcutaneous Electrical Nerve Stimulation)
                    - **Goal**: Pain management via Gait Control Theory or Endogenous Opioid Release.
                    - **Parameters**: High frequency (conventional) vs. Low frequency (acupuncture-like).
                    
                    ### NMES (Neuromuscular Electrical Stimulation)
                    - **Goal**: Muscle strengthening and re-education (e.g., VMO after ACL surgery).
                    - **Parameters**: Russian current or biphasic pulsed current with long phase duration.
                    """)
                .references(List.of("Cameron's Physical Agents", "Electrophysical Agents in PT"))
                .imageUrls(List.of("https://images.unsplash.com/photo-1581093458791-9f3c3900df4b?auto=format&fit=crop&q=80&w=800"))
                .build()
        );
        
        return fallbacks.get((int) (Math.random() * fallbacks.size()));
    }
}
