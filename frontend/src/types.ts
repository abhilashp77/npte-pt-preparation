export interface Question {
  id: number;
  question: string;
  options: string[];
  difficulty: string;
  system: string;
}

export interface SubmitAnswerRequest {
  questionId: number;
  selectedAnswer: string;
}

export interface SubmitAnswerResponse {
  correctAnswer: string;
  explanation: string;
  references: string[];
  images: string[];
}

export interface Topic {
  title: string;
  category: string;
  content: string;
  references: string[];
  imageUrls: string[];
}
