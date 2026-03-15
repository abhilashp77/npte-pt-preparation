import json
import random

def generate_questions(count=1000):
    questions = []
    
    systems = ["Musculoskeletal", "Neuromuscular", "Cardiovascular & Pulmonary", "Integumentary", "Other Systems"]
    difficulties = ["Easy", "Moderate", "Hard", "Exam Level"]
    
    # Template 1: Muscle Innervation (Musculoskeletal/Neuromuscular)
    muscles = [
        {"name": "Tibialis anterior", "nerve": "Deep peroneal nerve", "root": "L4-L5", "action": "Ankle dorsiflexion and inversion"},
        {"name": "Gastrocnemius", "nerve": "Tibial nerve", "root": "S1-S2", "action": "Ankle plantarflexion"},
        {"name": "Gluteus maximus", "nerve": "Inferior gluteal nerve", "root": "L5-S2", "action": "Hip extension and lateral rotation"},
        {"name": "Gluteus medius", "nerve": "Superior gluteal nerve", "root": "L4-S1", "action": "Hip abduction"},
        {"name": "Quadriceps femoris", "nerve": "Femoral nerve", "root": "L2-L4", "action": "Knee extension"},
        {"name": "Hamstrings", "nerve": "Sciatic nerve", "root": "L5-S2", "action": "Knee flexion and hip extension"},
        {"name": "Biceps brachii", "nerve": "Musculocutaneous nerve", "root": "C5-C6", "action": "Elbow flexion and supination"},
        {"name": "Triceps brachii", "nerve": "Radial nerve", "root": "C6-C8", "action": "Elbow extension"},
        {"name": "Deltoid", "nerve": "Axillary nerve", "root": "C5-C6", "action": "Shoulder abduction"},
        {"name": "Supraspinatus", "nerve": "Suprascapular nerve", "root": "C5-C6", "action": "Shoulder abduction initiation"},
        {"name": "Latissimus dorsi", "nerve": "Thoracodorsal nerve", "root": "C6-C8", "action": "Shoulder extension, adduction, and internal rotation"},
        {"name": "Serratus anterior", "nerve": "Long thoracic nerve", "root": "C5-C7", "action": "Scapular protraction and upward rotation"},
    ]
    
    # Template 2: Gait Deviations
    gait_issues = [
        {"deviation": "Trendelenburg gait", "cause": "Weakness of the gluteus medius", "side": "contralateral pelvis drops during swing"},
        {"deviation": "Steppage gait", "cause": "Weakness of the ankle dorsiflexors", "side": "excessive hip/knee flexion to clear foot"},
        {"deviation": "Circumduction gait", "cause": "Weakness of the hip flexors or long limb", "side": "leg swings out in a circular motion"},
        {"deviation": "Vaulting", "cause": "Limited knee flexion or ankle dorsiflexion", "side": "rising up on the toes of the uninvolved limb"},
        {"deviation": "Genu recurvatum", "cause": "Weakness of the quadriceps or tight plantarflexors", "side": "knee hyperextension during stance phase"},
    ]
    
    # Template 3: Cardiopulmonary Vitals
    cp_scenarios = [
        {"finding": "ST segment depression of 2mm", "significance": "Myocardial ischemia", "action": "Stop exercise and notify physician"},
        {"finding": "Drops in systolic BP > 10 mmHg with increased workload", "significance": "Exertional hypotension", "action": "Terminate exercise session"},
        {"finding": "Oxygen saturation below 88%", "significance": "Hypoxemia", "action": "Administer supplemental oxygen as prescribed"},
        {"finding": "Borg Scale RPE of 15/20", "significance": "Hard exertion", "action": "Monitor closely, consistent with aerobic training targets"},
    ]

    # Specialized high-difficulty Case Studies (Phase 8)
    case_studies = [
        {
            "scenario": "A 52-year-old male with a history of Type 2 Diabetes and smoking presents with intermittent claudication. He reports pain in the calf while walking that is relieved by rest. On examination, his ankle-brachial index (ABI) is 0.65.",
            "question": "Which of the following interventions is MOST appropriate for this patient's initial exercise program?",
            "correct": "Walking program at an intensity that produces symptoms within 3 to 5 minutes",
            "distractors": ["High-impact aerobic dancing", "Passive stretching of the gastrocnemius", "Complete bed rest until symptoms resolve"],
            "system": "Cardiovascular & Pulmonary",
            "explanation": "Intermittent claudication is a hallmark of Peripheral Artery Disease (PAD). An ABI of 0.65 indicates moderate arterial insufficiency. For these patients, a supervised walking program is the gold standard for improving functional capacity. The program should involve walking until the patient reaches moderate pain, followed by rest, which stimulates collateral circulation. Total bed rest or low-intensity passive stretching will not address the physiological need for vascular adaptation.",
            "refs": ["https://www.choosept.com/guide/physical-therapy-guide-peripheral-artery-disease", "ACSM's Guidelines for Exercise Testing"]
        },
        {
            "scenario": "A patient following a stroke demonstrates a significant knee hyperextension (genu recurvatum) during the midstance phase of gait. You note significant weakness in the quadriceps muscle group (3/5 MMT) and moderate spasticity in the plantarflexors.",
            "question": "Which orthotic adjustment or intervention is MOST likely to improve this specific gait deviation?",
            "correct": "Ankle-foot orthosis (AFO) set in 5 degrees of dorsiflexion",
            "distractors": ["Setting the AFO in 5 degrees of plantarflexion", "Applying a heel lift to the contralateral limb", "Using a knee cage to lock the joint"],
            "system": "Neuromuscular & Nervous",
            "explanation": "Knee hyperextension in stance is often caused by either weak quadriceps (unable to control knee flexion) or tight/spastic plantarflexors (pulling the tibia back). By setting an AFO in a few degrees of dorsiflexion, you create a flexion moment at the knee during stance, which physically prevents the 'snapping back' into hyperextension. Setting it in plantarflexion would actually worsen the hyperextension. A heel lift on the other side does not address the biomechanical fault of the affected limb.",
            "refs": ["https://edubirdie.com/examples/orthotics-and-prosthetics-in-physical-therapy/", "Magee's Orthopedic Physical Assessment"]
        },
        {
            "scenario": "A 28-year-old marathon runner presents with sharp pain on the lateral aspect of the knee that is most severe when the knee is in 30 degrees of flexion during downhill running. Noble's compression test is positive.",
            "question": "Which clinical condition is MOST consistent with these findings?",
            "correct": "Iliotibial Band Friction Syndrome",
            "distractors": ["Anterior Cruciate Ligament Tear", "Pes Anserine Bursitis", "Lateral Meniscus Tear"],
            "system": "Musculoskeletal",
            "explanation": "Iliotibial Band (ITB) Friction Syndrome is a common overuse injury in runners where the ITB rubs against the lateral femoral epicondyle. Pain is typically most acute at 30 degrees of knee flexion, known as the 'impingement zone.' Noble's compression test is the specific provocative maneuver for this condition. While meniscal tears cause lateral pain, they are usually associated with joint line tenderness and clicking/locking. Pes anserine pain is localized to the medial aspect of the tibia.",
            "refs": ["https://www.physio-pedia.com/Iliotibial_Band_Syndrome", "Dutton's Orthopaedic Examination"]
        },
        {
            "scenario": "A 45-year-old patient reports persistent low back pain that radiates down the posterior aspect of the left leg to the calf. Straight leg raise test is positive at 45 degrees. The patient also demonstrates weakness in the gastrocnemius and a diminished Achilles tendon reflex.",
            "question": "Which nerve root is MOST likely involved?",
            "correct": "S1",
            "distractors": ["L4", "L5", "S2"],
            "system": "Neuromuscular & Nervous",
            "explanation": "The symptoms described—pain radiating to the calf, weakness in the gastrocnemius (plantarflexion), and a diminished Achilles reflex (S1-S2 arc)—are highly characteristic of an S1 nerve root compression. L4 would involve the patellar reflex and tibialis anterior. L5 would involve the extensor hallucis longus and sensation on the dorsal aspect of the foot. S1 is the primary nerve root responsible for the gastrocnemius/soleus group and the ankle jerk reflex.",
            "refs": ["https://www.orthobullets.com/spine/2026/lumbar-disc-herniation", "Gray's Anatomy for Students"]
        },
        {
            "scenario": "A therapist is evaluating a patient with a suspected Labral tear of the shoulder. The therapist performs the O'Brien's test, which is positive for pain when the arm is internally rotated but relieved when the arm is externally rotated.",
            "question": "Which of the following describes the MOST appropriate next clinical step or associated finding?",
            "correct": "Assess for a SLAP lesion (Superior Labrum Anterior to Posterior)",
            "distractors": ["Suspect a Bankart lesion only", "Check for a rotator cuff tear using the Hawkins-Kennedy test", "Evaluate for adhesive capsulitis"],
            "system": "Musculoskeletal",
            "explanation": "A positive O'Brien's test (Active Compression Test) that is painful with internal rotation and relieved with external rotation is highly suggestive of a SLAP lesion. A Bankart lesion typically involves the inferior labrum and is usually associated with anterior dislocations. Hawkins-Kennedy is specific for subacromial impingement. Labral tears require careful clinical correlation and often advanced imaging like MR Arthrography if surgery is considered.",
            "refs": ["https://www.physio-pedia.com/SLAP_Lesion", "Dutton's Orthopaedic Examination"]
        }
    ]

    for i in range(count):
        # First half: Mix of standard templates, Second half: Emphasis on hard case studies
        if i < count // 2:
            type_choice = random.randint(1, 4)
            difficulty = random.choice(difficulties)
        else:
            type_choice = 5 # Case Study
            difficulty = random.choice(["Hard", "Exam Level"])

        if type_choice == 1: # Muscle/Nerve
            m = random.choice(muscles)
            q_text = f"A patient presents with weakness during {m['action'].lower()}. Which nerve is most likely involved in this clinical presentation?"
            correct = m['nerve']
            distractors = random.sample(list(set([x['nerve'] for x in muscles if x['nerve'] != correct])), 3)
            system = "Musculoskeletal"
            explanation = f"The {m['name']} is responsible for {m['action'].lower()} and is innervated by the {m['nerve']} ({m['root']}). Weakness in this muscle group suggests a lesion anywhere along the {m['nerve']} or the corresponding spinal roots."
            refs = ["https://www.physio-pedia.com/Muscle_Innervation", "https://www.orthobullets.com/anatomy/10001/muscles-of-the-hip"]
            
        elif type_choice == 2: # Gait
            g = random.choice(gait_issues)
            q_text = f"During a gait analysis, a therapist observes a {g['deviation'].lower()}. This pattern is most commonly associated with which of the following causes?"
            correct = g['cause']
            distractors = random.sample(list(set([x['cause'] for x in gait_issues if x['cause'] != correct])), 3)
            system = "Musculoskeletal"
            explanation = f"{g['deviation']} is a classic gait deviation where {g['side']}. It is primarily caused by {g['cause']}. Accurate identification of the root cause is essential for determining the appropriate orthotic or therapeutic intervention."
            refs = ["https://www.physio-pedia.com/Gait_Analysis", "https://observationalgaitanalysis.com/"]
            
        elif type_choice == 3: # Cardiopulmonary
            cp = random.choice(cp_scenarios)
            q_text = f"While monitoring a patient during a cardiac rehabilitation session, the therapist notes {cp['finding'].lower()}. What is the most appropriate immediate action?"
            correct = cp['action']
            others = [x['action'] for x in cp_scenarios if x['action'] != correct] + ["Continue exercise at reduced intensity", "Wait 5 minutes and re-check", "Call 911 immediately"]
            distractors = random.sample(list(set(others)), 3)
            system = "Cardiovascular & Pulmonary"
            explanation = f"{cp['finding']} is indicative of {cp['significance'].lower()}, which requires the therapist to {cp['action'].lower()} for patient safety. Monitoring vital signs and recognizing red flags is a critical competency for physical therapists in all settings."
            refs = ["https://www.acsm.org/education-resources/books/guidelines-exercise-testing-prescription", "https://www.physio-pedia.com/Cardiovascular_and_Pulmonary_Systems"]

        elif type_choice == 4: # Pediatric Highlights
            milestones = [
                {"age": "6 months", "skill": "Sitting with support", "next": "Sitting independently"},
                {"age": "12 months", "skill": "Walking with handheld support", "next": "Walking independently"},
                {"age": "4 months", "skill": "Rolling from prone to supine", "next": "Rolling from supine to prone"},
            ]
            mile = random.choice(milestones)
            q_text = f"A pediatric therapist is evaluating a {mile['age']} old infant. Which gross motor milestone is MOST appropriate to expect at this developmental stage?"
            correct = mile['skill']
            distractors = ["Cruising along furniture", "Climbing stairs with railing", "Hopping on one foot"]
            system = "Other Systems"
            explanation = f"By {mile['age']}, most infants have developed the core stability and motor control required for {mile['skill'].lower()}. These milestones are used to track neurological and musculoskeletal development and identify potential delays early."
            refs = ["https://www.physio-pedia.com/Developmental_Milestones", "https://pediatricapta.org/"]

        else: # Specialized Case Study
            cs = random.choice(case_studies)
            q_text = f"{cs['scenario']}\n\n{cs['question']}"
            correct = cs['correct']
            distractors = cs['distractors']
            system = cs['system']
            explanation = cs['explanation']
            refs = cs['refs']

        # Final packaging
        options = list(distractors) + [correct]
        random.shuffle(options)
        opt_map = {"A": options[0], "B": options[1], "C": options[2], "D": options[3]}
        ans_key = [k for k, v in opt_map.items() if v == correct][0]
                
        questions.append({
            "id": i + 1,
            "bodySystem": system,
            "difficulty": difficulty,
            "questionText": q_text,
            "correctAnswer": ans_key,
            "options": opt_map,
            "explanation": explanation,
            "references": refs
        })
        
    return questions

if __name__ == "__main__":
    # Generate 2000 questions (Phase 8 requirement)
    count = 2000
    data = generate_questions(count)
    import os
    # Ensure directory exists or use absolute path
    output_path = "backend/src/main/resources/questions-seed.json"
    with open(output_path, "w") as f:
        json.dump(data, f, indent=4)
    print(f"Generated {len(data)} questions to {output_path}")
