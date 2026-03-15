import { useState, useEffect } from 'react';
import { BookOpen, RefreshCw, ChevronLeft, Image as ImageIcon } from 'lucide-react';
import { fetchRandomTopic } from '../api';
import type { Topic } from '../types';
import ReactMarkdown from 'react-markdown';

export default function StudyMode({ onBack }: { onBack: () => void }) {
  const [topic, setTopic] = useState<Topic | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const loadTopic = async () => {
    setIsLoading(true);
    try {
      const data = await fetchRandomTopic();
      setTopic(data);
    } catch (error) {
      console.error('Failed to load topic:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadTopic();
  }, []);

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* Header Actions */}
      <div className="flex justify-between items-center mb-4">
        <button 
          onClick={onBack}
          className="flex items-center gap-2 text-slate-500 hover:text-brand-600 font-medium transition-colors"
        >
          <ChevronLeft size={20} /> Back to Questions
        </button>
        
        <button
          onClick={loadTopic}
          disabled={isLoading}
          className="flex items-center gap-2 px-4 py-2 bg-brand-50 text-brand-700 rounded-lg font-semibold hover:bg-brand-100 transition-all border border-brand-200 disabled:opacity-50"
        >
          <RefreshCw size={18} className={isLoading ? 'animate-spin' : ''} />
          {isLoading ? 'Loading Topic...' : 'Next Topic'}
        </button>
      </div>

      {isLoading ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center space-y-4">
          <div className="flex justify-center">
            <div className="w-12 h-12 border-4 border-brand-200 border-t-brand-600 rounded-full animate-spin"></div>
          </div>
          <p className="text-slate-500 font-medium italic">Our NPTE professor is preparing a topic review for you...</p>
        </div>
      ) : !topic ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center space-y-4">
          <p className="text-red-500 font-medium">Failed to load syllabus topic. Please try again.</p>
          <button 
            onClick={loadTopic}
            className="px-4 py-2 bg-brand-600 text-white rounded-lg hover:bg-brand-700 transition-colors"
          >
            Retry Fetch
          </button>
        </div>
      ) : (
        <div className="bg-white rounded-2xl shadow-xl border border-slate-200 overflow-hidden relative">
          <div className="absolute top-0 left-0 w-2 h-full bg-brand-500"></div>
          
          <div className="p-8 md:p-10">
            <div className="flex flex-wrap items-center gap-3 mb-4">
              <span className="px-3 py-1 bg-brand-50 text-brand-700 text-xs font-bold uppercase tracking-wider rounded-full border border-brand-100">
                {topic.category}
              </span>
              <span className="flex items-center gap-1 text-amber-600 font-semibold text-sm italic">
                Verified Syllabus Content
              </span>
            </div>

            <h2 className="text-3xl md:text-4xl font-extrabold text-slate-900 mb-8 tracking-tight">
              {topic.title}
            </h2>

            {/* Images Section */}
            {topic.imageUrls && topic.imageUrls.length > 0 && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-10">
                {topic.imageUrls.map((url: string, idx: number) => (
                  <div key={idx} className="relative aspect-video rounded-xl overflow-hidden border border-slate-200 shadow-sm group">
                    <img 
                      src={url} 
                      alt={`Reference ${idx + 1}`} 
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                      onError={(e) => {
                        (e.target as HTMLImageElement).style.display = 'none';
                      }}
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/40 to-transparent flex items-end p-3">
                      <span className="text-white text-xs font-medium flex items-center gap-1 drop-shadow-md">
                        <ImageIcon size={14} /> Clinical Illustration
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Content Section */}
            <div className="prose prose-slate max-w-none prose-headings:text-slate-900 prose-headings:font-bold prose-p:text-slate-700 prose-p:leading-relaxed prose-li:text-slate-700 text-lg">
              <ReactMarkdown>{topic.content}</ReactMarkdown>
            </div>

            {/* References Section */}
            {topic.references && topic.references.length > 0 && (
              <div className="mt-12 pt-8 border-t border-slate-100 text-center sm:text-left">
                <h4 className="flex items-center justify-center sm:justify-start gap-2 text-slate-900 font-bold mb-4">
                  <BookOpen size={20} className="text-brand-500" /> Key Study References
                </h4>
                <div className="flex flex-wrap gap-3">
                  {topic.references.map((ref: string, idx: number) => (
                    <div key={idx} className="flex items-center gap-2 text-slate-600 bg-slate-50 px-4 py-2 rounded-lg border border-slate-100 text-sm">
                      <div className="w-1.5 h-1.5 bg-brand-400 rounded-full"></div>
                      {ref}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
