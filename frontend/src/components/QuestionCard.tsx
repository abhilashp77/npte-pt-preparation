import type { Question } from '../types';
import { Target } from 'lucide-react';

interface QuestionCardProps {
  question: Question;
  selectedOption: string | null;
  onSelectOption: (option: string) => void;
  onSubmit: () => void;
  isSubmitting: boolean;
  isAnswered: boolean;
  correctAnswer?: string;
}

export default function QuestionCard({
  question,
  selectedOption,
  onSelectOption,
  onSubmit,
  isSubmitting,
  isAnswered,
  correctAnswer
}: QuestionCardProps) {

  // Letter mapping for options
  const optionLetters = ['A', 'B', 'C', 'D'];

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
      {/* Header Info */}
      <div className="bg-slate-50 px-6 py-4 flex flex-wrap gap-4 justify-between items-center border-b border-slate-100">
        <div className="flex items-center gap-2 text-slate-600 text-sm font-medium">
          {/* Question ID hidden as per user request */}
        </div>
        <div className="flex gap-3">
          <span className="px-3 py-1 bg-indigo-50 text-indigo-700 rounded-full text-xs font-semibold uppercase tracking-wider">
            {question.system}
          </span>
          <span className="px-3 py-1 bg-amber-50 text-amber-700 rounded-full text-xs font-semibold uppercase tracking-wider flex items-center gap-1">
            <Target size={12} /> {question.difficulty}
          </span>
        </div>
      </div>

      {/* Question Body */}
      <div className="p-6 sm:p-8">
        <h2 className="text-xl sm:text-2xl font-semibold text-slate-800 leading-snug mb-8">
          {question.id}. {question.question}
        </h2>

        {/* Options */}
        <div className="space-y-3">
          {question.options.map((optionText, index) => {
            const letter = optionLetters[index];
            const isSelected = selectedOption === letter;

            // Post-answer styles
            const isCorrect = isAnswered && letter === correctAnswer;
            const isWrongSelected = isAnswered && isSelected && letter !== correctAnswer;

            let buttonClass = "w-full text-left p-4 rounded-xl border-2 transition-all duration-200 flex items-start gap-4 ";

            if (isAnswered) {
              if (isCorrect) {
                buttonClass += "border-brand-500 bg-brand-50 text-brand-900 shadow-sm";
              } else if (isWrongSelected) {
                buttonClass += "border-red-400 bg-red-50 text-red-900";
              } else {
                buttonClass += "border-slate-100 bg-white text-slate-400 opacity-60";
              }
            } else {
              if (isSelected) {
                buttonClass += "border-brand-500 bg-brand-50 text-brand-900 shadow-md transform scale-[1.01]";
              } else {
                buttonClass += "border-slate-200 bg-white text-slate-700 hover:border-brand-300 hover:bg-slate-50 hover:shadow-sm";
              }
            }

            return (
              <button
                key={letter}
                onClick={() => !isAnswered && onSelectOption(letter)}
                disabled={isAnswered}
                className={buttonClass}
              >
                <div className={`flex-shrink-0 w-8 h-8 flex items-center justify-center rounded-lg font-bold text-sm transition-colors ${(isSelected && !isAnswered) || isCorrect ? 'bg-brand-500 text-white' :
                    isWrongSelected ? 'bg-red-500 text-white' : 'bg-slate-100 text-slate-500'
                  }`}>
                  {letter}
                </div>
                <div className="pt-1 text-[1.05rem]">{optionText}</div>
              </button>
            );
          })}
        </div>

        {/* Actions */}
        {!isAnswered && (
          <div className="mt-8 flex justify-end">
            <button
              onClick={onSubmit}
              disabled={!selectedOption || isSubmitting}
              className={`px-8 py-3 rounded-xl font-bold tracking-wide transition-all shadow-sm flex items-center gap-2 ${!selectedOption || isSubmitting
                  ? 'bg-slate-200 text-slate-400 cursor-not-allowed'
                  : 'bg-brand-500 text-white hover:bg-brand-600 hover:shadow-md hover:-translate-y-0.5'
                }`}
            >
              {isSubmitting ? 'Submitting...' : 'Show Answer'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
