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

    for i in range(count):
        type_choice = random.randint(1, 4)
        
        if type_choice == 1: # Muscle/Nerve
            m = random.choice(muscles)
            q_text = f"A patient presents with weakness during {m['action'].lower()}. Which nerve is most likely involved in this clinical presentation?"
            correct = m['nerve']
            others = [x['nerve'] for x in muscles if x['nerve'] != correct]
            distractors = random.sample(list(set(others)), 3)
            system = "Musculoskeletal"
            explanation = f"The {m['name']} is responsible for {m['action'].lower()} and is innervated by the {m['nerve']} ({m['root']})."
            refs = ["Dutton's Orthopaedic Examination", "Gray's Anatomy for Students"]
            
        elif type_choice == 2: # Gait
            g = random.choice(gait_issues)
            q_text = f"During a gait analysis, a therapist observes a {g['deviation'].lower()}. This pattern is most commonly associated with which of the following causes?"
            correct = g['cause']
            others = [x['cause'] for x in gait_issues if x['cause'] != correct]
            distractors = random.sample(list(set(others)), 3)
            system = "Musculoskeletal"
            explanation = f"{g['deviation']} is a classic gait deviation where {g['side']}. It is primarily caused by {g['cause']}."
            refs = ["Observational Gait Analysis: A Visual Guide", "Magee's Orthopedic Physical Assessment"]
            
        elif type_choice == 3: # Cardiopulmonary
            cp = random.choice(cp_scenarios)
            q_text = f"While monitoring a patient during a cardiac rehabilitation session, the therapist notes {cp['finding'].lower()}. What is the most appropriate immediate action?"
            correct = cp['action']
            others = [x['action'] for x in cp_scenarios if x['action'] != correct]
            # Add general distractors if list is short
            if len(others) < 3: others += ["Continue exercise at reduced intensity", "Wait 5 minutes and re-check", "Call 911 immediately"]
            distractors = random.sample(list(set(others)), 3)
            system = "Cardiovascular & Pulmonary"
            explanation = f"{cp['finding']} is indicative of {cp['significance'].lower()}, which requires the therapist to {cp['action'].lower()} for patient safety."
            refs = ["ACSM's Guidelines for Exercise Testing and Prescription", "Essentials of Cardiopulmonary Physical Therapy"]

        else: # Pediatric/Other
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
            explanation = f"By {mile['age']}, most infants have developed the core stability and motor control required for {mile['skill'].lower()}."
            refs = ["Campbell's Physical Therapy for Children", "Tecklin's Pediatric Physical Therapy"]

        # Final packaging
        options = distractors + [correct]
        random.shuffle(options)
        
        opt_map = {
            "A": options[0],
            "B": options[1],
            "C": options[2],
            "D": options[3]
        }
        
        ans_key = "A"
        for k, v in opt_map.items():
            if v == correct:
                ans_key = k
                
        questions.append({
            "id": i + 10, # Keep space for existing
            "bodySystem": system,
            "difficulty": random.choice(difficulties),
            "questionText": q_text,
            "correctAnswer": ans_key,
            "options": opt_map,
            "explanation": explanation,
            "references": refs
        })
        
    return questions

if __name__ == "__main__":
    data = generate_questions(1000)
    with open("backend/src/main/resources/questions-seed.json", "w") as f:
        json.dump(data, f, indent=4)
    print(f"Generated {len(data)} questions to backend/src/main/resources/questions-seed.json")
