import { useState } from 'react';
import type { SubmitAnswerResponse, FollowUpAnswerResponse } from '../types';
import { fetchFollowUpOptions, fetchFollowUpAnswer } from '../api';
import { ArrowRight, BookMarked, CheckCircle, Info, MessageCircleQuestion, Loader2, Lightbulb } from 'lucide-react';

interface ExplanationCardProps {
  explanation: SubmitAnswerResponse;
  originalQuestion: string;
  onNextQuestion: () => void;
}

export default function ExplanationCard({
  explanation,
  originalQuestion,
  onNextQuestion,
}: ExplanationCardProps) {
  const [followUpOptions, setFollowUpOptions] = useState<string[]>([]);
  const [isLoadingOptions, setIsLoadingOptions] = useState(false);
  const [followUpAnswer, setFollowUpAnswer] = useState<FollowUpAnswerResponse | null>(null);
  const [isLoadingAnswer, setIsLoadingAnswer] = useState(false);
  const [showFollowUps, setShowFollowUps] = useState(false);

  const handleAskFollowUp = async () => {
    if (followUpOptions.length > 0) {
      setShowFollowUps(!showFollowUps);
      return;
    }
    setIsLoadingOptions(true);
    setShowFollowUps(true);
    try {
      const response = await fetchFollowUpOptions({
        question: originalQuestion,
        selectedAnswer: '',
        correctAnswer: explanation.correctAnswer,
        explanation: explanation.explanation,
      });
      setFollowUpOptions(response.options);
    } catch (error) {
      console.error('Failed to fetch follow-up options', error);
      setFollowUpOptions([
        'Can you explain the underlying anatomy involved?',
        'What are the key differential diagnoses to consider?',
        'What evidence-based interventions are most effective?',
      ]);
    } finally {
      setIsLoadingOptions(false);
    }
  };

  const handleSelectFollowUp = async (followUpQuestion: string) => {
    setIsLoadingAnswer(true);
    setFollowUpAnswer(null);
    try {
      const response = await fetchFollowUpAnswer({
        originalQuestion,
        correctAnswer: explanation.correctAnswer,
        explanation: explanation.explanation,
        followUpQuestion,
      });
      setFollowUpAnswer(response);
    } catch (error) {
      console.error('Failed to get follow-up answer', error);
      setFollowUpAnswer({
        question: followUpQuestion,
        answer: 'Unable to generate an answer at this time. Please try again later.',
      });
    } finally {
      setIsLoadingAnswer(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden animate-in fade-in slide-in-from-bottom-6 duration-700 relative">
      <div className="absolute top-0 left-0 w-2 h-full bg-brand-500"></div>

      <div className="p-6 sm:p-8">
        <div className="flex flex-wrap justify-between items-start gap-6 mb-6">
          <div>
            <h3 className="text-xl font-bold flex items-center gap-2 text-slate-900 border-b-2 border-brand-100 pb-2 inline-flex">
              <CheckCircle className="text-brand-500" />
              Correct Answer: {explanation.correctAnswer}
            </h3>
          </div>

          <div className="flex gap-3">
            <button
              onClick={handleAskFollowUp}
              disabled={isLoadingOptions}
              className="px-6 py-3 bg-brand-50 text-brand-700 rounded-xl font-bold hover:bg-brand-100 transition-all shadow-sm border border-brand-200 flex items-center gap-2 disabled:opacity-50"
            >
              {isLoadingOptions ? (
                <>
                  <Loader2 size={18} className="animate-spin" /> Loading...
                </>
              ) : (
                <>
                  <MessageCircleQuestion size={18} /> Ask Follow-up Questions
                </>
              )}
            </button>

            <button
              onClick={onNextQuestion}
              className="px-6 py-3 bg-slate-900 text-white rounded-xl font-bold hover:bg-slate-800 transition-all shadow-md flex items-center gap-2 hover:shadow-lg hover:-translate-y-0.5"
            >
              Next Question <ArrowRight size={18} />
            </button>
          </div>
        </div>

        <div className="space-y-8">
          <div className="prose prose-slate max-w-none text-slate-700 leading-relaxed text-[1.05rem]">
            <h4 className="flex items-center gap-2 text-slate-900 font-semibold mb-3">
              <Info size={20} className="text-indigo-500" /> Explanation
            </h4>
            <div className="bg-indigo-50/50 p-6 rounded-xl border border-indigo-100/50 whitespace-pre-line">
              {explanation.explanation}
            </div>
          </div>

          {explanation.images && explanation.images.length > 0 && (
            <div>
              <h4 className="font-semibold text-slate-900 mb-3">Reference Images</h4>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {explanation.images.map((img, idx) => (
                  <img key={idx} src={img} alt="Reference" className="rounded-xl border border-slate-200 shadow-sm" />
                ))}
              </div>
            </div>
          )}

          {explanation.references && explanation.references.length > 0 && (
            <div>
              <h4 className="flex items-center gap-2 text-slate-900 font-semibold mb-3">
                <BookMarked size={18} className="text-amber-500" /> Sources
              </h4>
              <ul className="list-disc list-inside space-y-1 text-slate-600 bg-amber-50/30 p-4 rounded-xl border border-amber-100">
                {explanation.references.map((ref, i) => {
                  const isUrl = ref.trim().toLowerCase().startsWith('http');
                  const normalizedRef = ref.trim();
                  return (
                    <li key={i}>
                      {isUrl ? (
                        <a 
                          href={normalizedRef} 
                          target="_blank" 
                          rel="noopener noreferrer"
                          className="text-brand-600 hover:text-brand-700 underline break-all"
                        >
                          {ref}
                        </a>
                      ) : ref}
                    </li>
                  );
                })}
              </ul>
            </div>
          )}

          {/* Follow-up Questions Section */}
          {showFollowUps && (
            <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
              <h4 className="flex items-center gap-2 text-slate-900 font-semibold mb-3">
                <MessageCircleQuestion size={20} className="text-brand-500" /> Follow-up Questions
              </h4>

              {isLoadingOptions ? (
                <div className="flex items-center justify-center py-8 text-slate-400">
                  <Loader2 className="animate-spin mr-2" size={24} />
                  <span className="font-medium">Generating follow-up questions...</span>
                </div>
              ) : (
                <div className="space-y-2">
                  {followUpOptions.map((option, idx) => (
                    <button
                      key={idx}
                      onClick={() => handleSelectFollowUp(option)}
                      disabled={isLoadingAnswer}
                      className="w-full text-left px-5 py-4 bg-gradient-to-r from-slate-50 to-brand-50/30 rounded-xl border border-slate-200 hover:border-brand-300 hover:from-brand-50 hover:to-brand-100/30 transition-all group flex items-start gap-3 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm hover:shadow-md"
                    >
                      <span className="shrink-0 w-7 h-7 bg-brand-100 text-brand-700 rounded-full flex items-center justify-center text-sm font-bold group-hover:bg-brand-200 transition-colors mt-0.5">
                        {idx + 1}
                      </span>
                      <span className="text-slate-700 font-medium group-hover:text-brand-800 transition-colors leading-relaxed">
                        {option}
                      </span>
                    </button>
                  ))}
                </div>
              )}

              {/* Follow-up Answer */}
              {isLoadingAnswer && (
                <div className="mt-4 flex items-center justify-center py-8 text-slate-400 bg-slate-50/50 rounded-xl border border-slate-100">
                  <Loader2 className="animate-spin mr-2" size={24} />
                  <span className="font-medium">Thinking...</span>
                </div>
              )}

              {followUpAnswer && !isLoadingAnswer && (
                <div className="mt-4 bg-gradient-to-br from-emerald-50 to-teal-50/50 p-6 rounded-xl border border-emerald-200/60 animate-in fade-in slide-in-from-bottom-4 duration-500">
                  <h5 className="flex items-center gap-2 text-emerald-800 font-semibold mb-3">
                    <Lightbulb size={18} className="text-emerald-500" />
                    {followUpAnswer.question}
                  </h5>
                  <div className="text-slate-700 leading-relaxed whitespace-pre-line">
                    {followUpAnswer.answer}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
