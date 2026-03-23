import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchRandomQuestion, submitAnswer } from '../api';
import QuestionCard from './QuestionCard';
import ExplanationCard from './ExplanationCard';
import type { Question, SubmitAnswerResponse } from '../types';
import { Loader2 } from 'lucide-react';

export default function QuestionContainer() {
  const queryClient = useQueryClient();
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const [explanationData, setExplanationData] = useState<SubmitAnswerResponse | null>(null);

  // Fetch initial question from DB
  const { data: question, isLoading: isQuestionLoading, isError, refetch } = useQuery<Question>({
    queryKey: ['randomQuestion'],
    queryFn: async () => {
      const prevQuestion = queryClient.getQueryData<Question>(['randomQuestion']);
      return fetchRandomQuestion(prevQuestion?.id);
    },
    gcTime: 0
  });

  // Submit Answer Mutation
  const mutation = useMutation({
    mutationFn: submitAnswer,
    onSuccess: (data) => {
      setExplanationData(data);
    }
  });

  const handleSubmit = () => {
    if (question && selectedOption) {
      mutation.mutate({
        questionId: question.id,
        selectedAnswer: selectedOption
      });
    }
  };

  // Next Question = pulls from database
  const handleNextQuestion = () => {
    setSelectedOption(null);
    setExplanationData(null);
    queryClient.invalidateQueries({ queryKey: ['randomQuestion'] });
    refetch();
  };

  if (isQuestionLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-slate-500">
        <Loader2 className="animate-spin mb-4" size={48} />
        <p className="text-lg font-medium animate-pulse">Loading question...</p>
      </div>
    );
  }

  if (isError || !question) {
    return (
      <div className="bg-red-50 text-red-600 p-6 rounded-xl border border-red-100 text-center">
        <h3 className="font-semibold mb-2">Failed to load question</h3>
        <button 
          onClick={() => refetch()}
          className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors shadow-sm"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">

      <QuestionCard 
        question={question} 
        selectedOption={selectedOption}
        onSelectOption={setSelectedOption}
        onSubmit={handleSubmit}
        isSubmitting={mutation.isPending}
        isAnswered={!!explanationData}
        correctAnswer={explanationData?.correctAnswer}
      />

      {explanationData && (
        <ExplanationCard 
          explanation={explanationData} 
          originalQuestion={question.question}
          onNextQuestion={handleNextQuestion}
        />
      )}
    </div>
  );
}
