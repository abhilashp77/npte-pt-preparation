import { useState } from 'react';
import { Activity, BookOpen, GraduationCap, UserCircle } from 'lucide-react';
import QuestionContainer from './components/QuestionContainer';
import StudyMode from './components/StudyMode';

function App() {
  const [view, setView] = useState<'practice' | 'study'>('practice');

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      {/* Navbar */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-10 shadow-sm">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div
              className="flex items-center gap-2 cursor-pointer transition-transform hover:scale-105"
              onClick={() => setView('practice')}
            >
              <div className="bg-brand-500 p-2 rounded-lg text-white shadow-sm shadow-brand-200">
                <Activity size={24} />
              </div>
              <h1 className="text-xl font-bold text-slate-900 tracking-tight">NPTE Practice Portal</h1>
            </div>

            <nav className="flex items-center gap-6">
              <button
                onClick={() => setView('practice')}
                className={`flex items-center gap-1.5 font-semibold transition-all py-1.5 px-3 rounded-lg ${view === 'practice'
                  ? 'text-brand-700 bg-brand-50'
                  : 'text-slate-500 hover:text-brand-600 hover:bg-slate-50'
                  }`}
              >
                <BookOpen size={18} />
                <span>Questions</span>
              </button>

              <button
                onClick={() => setView('study')}
                className={`flex items-center gap-1.5 font-semibold transition-all py-1.5 px-3 rounded-lg ${view === 'study'
                  ? 'text-brand-700 bg-brand-50'
                  : 'text-slate-500 hover:text-brand-600 hover:bg-slate-50'
                  }`}
              >
                <GraduationCap size={18} />
                <span>Flash Cards</span>
              </button>

              <div className="h-9 w-9 bg-slate-100 rounded-full flex items-center justify-center text-slate-500 hover:bg-slate-200 cursor-pointer transition-colors border border-slate-200">
                <UserCircle size={24} />
              </div>
            </nav>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 w-full max-w-4xl mx-auto px-4 sm:px-6 py-8">
        {view === 'practice' ? (
          <QuestionContainer />
        ) : (
          <StudyMode onBack={() => setView('practice')} />
        )}
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-8 mt-auto">
        <div className="max-w-5xl mx-auto px-4 text-center">
          <p className="text-slate-900 font-bold mb-1">NPTE Excellence Platform</p>
          <p className="text-slate-500 text-sm italic">
            &copy; {new Date().getFullYear()} Dedicated to your clinical success.
          </p>
        </div>
      </footer>
    </div>
  )
}

export default App;
