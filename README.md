# NPTE PT Exam Practice Portal

## Project Goal
Build a full-stack **NPTE (National Physical Therapy Examination) practice portal** that generates and displays **realistic NPTE-style questions** one at a time.

The system should simulate the experience of major NPTE prep platforms such as:

- PEAT
- Scorebuilders
- TherapyEd
- Final Frontier
- Typical PT

The portal must follow the **2024 NPTE Test Content Outline published by FSBPT**.

Reference syllabus:
https://www.fsbpt.org/Portals/0/documents/free-resources/2024-PT-Test-Content-Outline-with-rubrics_1_1_20230825.pdf

The NPTE primarily tests clinical decision-making using scenario-based multiple-choice questions across body systems. Most questions are from **musculoskeletal and neuromuscular systems**, followed by cardiopulmonary and other systems. 

Approximate domain distribution:

Body Systems (~72%)
- Musculoskeletal
- Neuromuscular & Nervous
- Cardiovascular & Pulmonary
- Integumentary
- Metabolic & Endocrine
- GI / GU
- Lymphatic
- System Interactions

Non-System Domains (~28%)
- Equipment & Assistive Devices
- Therapeutic Modalities
- Safety & Protection
- Professional Responsibilities
- Research & Evidence Based Practice

The NPTE contains **200 multiple-choice questions** with a strong focus on clinical case scenarios. 

---

# Local Development Setup

## Prerequisites

- **Java 17+** (JDK)
- **Node.js 18+** and **npm**
- **PostgreSQL 15** (via Docker or local install)
- **NVIDIA API Key** (optional, for AI follow-up questions)

## 1. Start PostgreSQL

Using Docker (recommended):

```bash
docker-compose up -d
```

This starts a PostgreSQL container with:
- **Host:** `localhost:5432`
- **Database:** `npte_portal`
- **User:** `npte`
- **Password:** `password`

Or if you have PostgreSQL installed locally, create the database:

```bash
createdb -U npte npte_portal
```

## 2. Set Environment Variables (optional)

For AI-powered follow-up questions, set your NVIDIA API key:

```bash
export NVIDIA_API_KEY=nvapi-your-key-here
```

Without this key, follow-up questions will use fallback responses.

## 3. Start the Backend

```bash
cd backend
./gradlew bootRun
```

The backend starts on **http://localhost:8080**. On first run, it automatically:
- Creates database tables via Hibernate
- Seeds questions from `questions-seed.json`
- Seeds topics from `topics-seed.json`

## 4. Start the Frontend

In a new terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on **http://localhost:5173**.

## 5. Open the App

Navigate to **http://localhost:5173** in your browser.

## Quick Start (all commands)

```bash
# Terminal 1: Start PostgreSQL
docker-compose up -d

# Terminal 2: Start Backend
cd backend
./gradlew bootRun

# Terminal 3: Start Frontend
cd frontend
npm install
npm run dev
```

## Stopping the App

```bash
# Stop frontend: Ctrl+C in the frontend terminal
# Stop backend: Ctrl+C in the backend terminal

# Stop PostgreSQL (Docker)
docker-compose down

# Stop PostgreSQL and delete data
docker-compose down -v
```

##### kill process on port 8080
```
lsof -ti:8080 | xargs kill -9
```
---

# Tech Stack

## Frontend
React
TypeScript
Vite or Next.js
Tailwind CSS
React Query

## Backend
Java Spring Boot

## Database
PostgreSQL

## Optional AI Question Generation
OpenAI API

---

# Core Features

See questions manually by calling post API: `curl -X POST https://npte-backend.onrender.com/api/admin/seed`

## 1 Question Portal

The portal must show **ONE question at a time**.

Example flow:

User opens portal  
↓  
Question appears  
↓  
User selects answer  
↓  
User clicks "Show Answer"  
↓  
Correct answer + explanation appear  
↓  
User clicks "Next Question"

---

# Question UI Layout

Header

NPTE Practice Portal

Main Question Card

Question number

Question text

Multiple choice answers (A-D)

Buttons

Submit Answer  
Show Explanation  
Next Question

---

# Example Question UI

Question #12

A 64-year-old patient with chronic obstructive pulmonary disease is performing aerobic training in pulmonary rehabilitation.

Which oxygen saturation level should the therapist generally maintain during exercise?

A. 80%  
B. 85%  
C. 88%  
D. 96%

Buttons

Submit  
Show Answer

---

# Explanation Section

When "Show Answer" is clicked display:

Correct Answer

Explanation

Clinical reasoning

Why other answers are wrong

Sources

Images if required

Example:

Correct Answer: C. 88%

Explanation:
During exercise training in patients with COPD, oxygen saturation should generally remain above 88% to prevent hypoxemia and maintain safe oxygen delivery to tissues.

Why the others are incorrect:

A: 80% is severe hypoxemia  
B: 85% still unsafe  
D: 96% is ideal but not necessary during exercise

Sources

ACSM Guidelines for Exercise Testing  
Cardiopulmonary Physical Therapy  
FSBPT Content Outline

Images

If explanation involves:

anatomy  
orthopedic tests  
neurological pathways  
assistive devices  
gait abnormalities  
imaging

display relevant educational images.

---

# Question Generation Requirements

Questions must follow **NPTE exam patterns**.

Sources to mimic style:

PEAT exams  
Scorebuilders  
TherapyEd  
NPTE prep books  
Barnes & Noble NPTE study guides

Question types to generate:

Clinical case scenario  
Best intervention selection  
Differential diagnosis  
Contraindications  
Outcome measure interpretation  
Gait analysis  
Neurological lesion localization  
Assistive device prescription  
Safety decisions  
Imaging interpretation

---

# Question Difficulty Levels

Each question must include difficulty:

Easy  
Moderate  
Exam Level

Default difficulty = Exam Level

---

# Question Data Model

Question object format
{
id: string,
category: string,
bodySystem: string,
difficulty: string,
question: string,
options: [
"A",
"B",
"C",
"D"
],
correctAnswer: string,
explanation: string,
incorrectRationale: {
"A": "",
"B": "",
"C": "",
"D": ""
},
references: [],
images: []
}


---

# Backend API Design

## Get Question

GET

/api/questions/random

Response

{
question,
options,
difficulty,
system
}

---

## Submit Answer

POST

/api/questions/submit

Body

{
questionId,
selectedAnswer
}


Response


{
correctAnswer,
explanation,
references,
images
}


---

## Next Question

GET

/api/questions/next

---

# Database Tables

questions

id  
question_text  
body_system  
difficulty  

answers

question_id  
option_a  
option_b  
option_c  
option_d  
correct_answer  

explanations

question_id  
explanation_text  
references  

images

question_id  
image_url  

---

# Question Distribution Logic

Generate questions based on NPTE blueprint.

Musculoskeletal ~25%  
Neuromuscular ~22%  
Cardiopulmonary ~13%  
Integumentary ~5%  
Other systems ~5%

Non-systems ~28%

---

# User Features

Progress tracking

Questions attempted  
Correct answers  
Accuracy percentage

Topic analytics

Weak topics  
Strong topics

Bookmark questions

Add personal notes

---

# Study Modes

## Study Mode

Immediate explanation  
Unlimited time

## Exam Mode

Timed questions  
No explanation until end  
Simulate NPTE

---

# Analytics Dashboard

Show

Accuracy by body system

Weak topic detection

Progress chart

Daily streak

---

# AI Question Generation

The system should generate questions by analyzing patterns from NPTE prep resources.

Rules:

Use realistic clinical scenarios

Avoid repeating questions

Match NPTE difficulty

Ensure clinical reasoning

---

# Example Generated Question

A patient presents with L4 nerve root compression.

Which muscle is most likely weakened?

A. Tibialis anterior  
B. Gastrocnemius  
C. Gluteus maximus  
D. Peroneus longus  

Correct Answer

A. Tibialis anterior

Explanation

L4 myotome contributes significantly to ankle dorsiflexion via tibialis anterior.

Images

Lumbar dermatomes chart

---

# Optional Advanced Features

Adaptive learning

AI tutor for explanations

Topic mastery scoring

Daily quiz generator

Leaderboard

---

# Expected Output

The system must produce:

React frontend question portal

Spring Boot backend API

Question database

Question generator system

Explanation engine

Image support

Progress analytics

---

# End Goal

A realistic **NPTE exam practice platform similar to UWorld / TrueLearn / Scorebuilders question banks.**
