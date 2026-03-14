import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchRandomQuestion, submitAnswer, fetchAiGeneratedQuestion } from '../api';
import QuestionCard from './QuestionCard';
import ExplanationCard from './ExplanationCard';
import type { Question, SubmitAnswerResponse } from '../types';
import { Loader2 } from 'lucide-react';

export default function QuestionContainer() {
  const queryClient = useQueryClient();
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const [explanationData, setExplanationData] = useState<SubmitAnswerResponse | null>(null);
  const [isGeneratingNext, setIsGeneratingNext] = useState(false);
  const [aiError, setAiError] = useState<string | null>(null);

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

  // Next Question = pulls from database (1000 questions)
  const handleNextQuestion = () => {
    setSelectedOption(null);
    setExplanationData(null);
    setAiError(null);
    queryClient.invalidateQueries({ queryKey: ['randomQuestion'] });
    refetch();
  };

  // Explicit AI Generation
  const handleGenerateAiQuestion = async () => {
    setIsGeneratingNext(true);
    setSelectedOption(null);
    setExplanationData(null);
    setAiError(null);
    try {
      const aiQuestion = await fetchAiGeneratedQuestion();
      queryClient.setQueryData(['randomQuestion'], aiQuestion);
    } catch (error) {
      console.error("Failed to generate next question", error);
      setAiError("AI credits exhausted. No new questions can be generated right now. Falling back to database questions.");
      // Fallback to DB question
      queryClient.invalidateQueries({ queryKey: ['randomQuestion'] });
      refetch();
    } finally {
      setIsGeneratingNext(false);
    }
  };

  if (isQuestionLoading || isGeneratingNext) {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-slate-500">
        <Loader2 className="animate-spin mb-4" size={48} />
        <p className="text-lg font-medium animate-pulse">
            {isGeneratingNext ? "AI is generating a new clinical scenario..." : "Loading question..."}
        </p>
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

      {aiError && (
        <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 flex items-start gap-3">
          <span className="text-amber-500 text-xl mt-0.5">⚠️</span>
          <div className="flex-1">
            <h4 className="font-semibold text-amber-800 mb-1">AI Credits Exhausted</h4>
            <p className="text-amber-700 text-sm">{aiError}</p>
          </div>
          <button 
            onClick={() => setAiError(null)} 
            className="text-amber-400 hover:text-amber-600 text-lg font-bold"
          >
            ✕
          </button>
        </div>
      )}

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
          onNextQuestion={handleNextQuestion}
          onGenerateAi={handleGenerateAiQuestion}
          isGeneratingAi={isGeneratingNext}
        />
      )}
    </div>
  );
}

