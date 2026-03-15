import axios from 'axios';
import type { Question, SubmitAnswerRequest, SubmitAnswerResponse } from './types';

const getBaseUrl = () => {
  const envUrl = import.meta.env.VITE_API_BASE_URL;
  if (!envUrl) return 'http://localhost:8080/api';
  
  // If the URL doesn't start with http/https, prepend https:// (Render provides just the host)
  const normalizedUrl = envUrl.startsWith('http') ? envUrl : `https://${envUrl}`;
  return normalizedUrl.endsWith('/api') ? normalizedUrl : `${normalizedUrl}/api`;
};

const apiClient = axios.create({
  baseURL: getBaseUrl(),
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
