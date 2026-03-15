import type { SubmitAnswerResponse } from '../types';
import { ArrowRight, BookMarked, CheckCircle, Info, Sparkles } from 'lucide-react';

interface ExplanationCardProps {
  explanation: SubmitAnswerResponse;
  onNextQuestion: () => void;
  onGenerateAi: () => void;
  isGeneratingAi: boolean;
}

export default function ExplanationCard({
  explanation,
  onNextQuestion,
  onGenerateAi,
  isGeneratingAi
}: ExplanationCardProps) {
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
              onClick={onGenerateAi}
              disabled={isGeneratingAi}
              className="px-6 py-3 bg-brand-50 text-brand-700 rounded-xl font-bold hover:bg-brand-100 transition-all shadow-sm border border-brand-200 flex items-center gap-2 disabled:opacity-50"
            >
              {isGeneratingAi ? 'Generating...' : 'Generate next question with AI'} <Sparkles size={18} />
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
        </div>
      </div>
    </div>
  );
}
