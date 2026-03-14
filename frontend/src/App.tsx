import { Activity, BookOpen, UserCircle } from 'lucide-react';
import QuestionContainer from './components/QuestionContainer';

function App() {
  return (
    <div className="min-h-screen flex flex-col">
      {/* Navbar */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-10 shadow-sm">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-2">
              <div className="bg-brand-500 p-2 rounded-lg text-white">
                <Activity size={24} />
              </div>
              <h1 className="text-xl font-bold text-slate-900 tracking-tight">NPTE Practice Portal</h1>
            </div>
            
            <nav className="flex items-center gap-6">
              <a href="#" className="text-slate-500 hover:text-brand-600 font-medium flex items-center gap-1 transition-colors">
                <BookOpen size={18} />
                <span>Practice</span>
              </a>
              <div className="h-8 w-8 bg-slate-100 rounded-full flex items-center justify-center text-slate-500 hover:bg-slate-200 cursor-pointer transition-colors">
                <UserCircle size={24} />
              </div>
            </nav>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 w-full max-w-4xl mx-auto px-4 sm:px-6 py-8">
        <QuestionContainer />
      </main>
      
      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-6 mt-auto">
        <div className="max-w-5xl mx-auto px-4 text-center text-slate-500 text-sm">
          &copy; {new Date().getFullYear()} NPTE Practice Platform. Focus on your clinical reasoning.
        </div>
      </footer>
    </div>
  )
}

export default App;
