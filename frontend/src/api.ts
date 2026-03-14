import axios from 'axios';
import type { Question, SubmitAnswerRequest, SubmitAnswerResponse } from './types';

const apiClient = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080') + '/api',
});

export const fetchRandomQuestion = async (excludeId?: number): Promise<Question> => {
  const params = excludeId ? { excludeId, t: Date.now() } : { t: Date.now() };
  const { data } = await apiClient.get<Question>('/questions/random', { params });
  return data;
};

export const submitAnswer = async (request: SubmitAnswerRequest): Promise<SubmitAnswerResponse> => {
  const { data } = await apiClient.post<SubmitAnswerResponse>('/questions/submit', request);
  return data;
};

export const fetchAiGeneratedQuestion = async (): Promise<Question> => {
  const { data } = await apiClient.get<Question>('/questions/generate');
  return data;
};
