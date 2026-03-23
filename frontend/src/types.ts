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

export interface FollowUpRequest {
  question: string;
  selectedAnswer: string;
  correctAnswer: string;
  explanation: string;
}

export interface FollowUpAnswerRequest {
  originalQuestion: string;
  correctAnswer: string;
  explanation: string;
  followUpQuestion: string;
}

export interface FollowUpOptionsResponse {
  options: string[];
}

export interface FollowUpAnswerResponse {
  question: string;
  answer: string;
}
