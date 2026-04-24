import React from 'react';
import { Toaster } from 'react-hot-toast';
import { Users, LogOut } from 'lucide-react';

export const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-900">
      <Toaster position="top-right" />
      
      {/* Header Premium / Glassmorphism */}
      <header className="sticky top-0 z-50 backdrop-blur-md bg-white/70 border-b border-slate-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-purple-600 flex items-center justify-center shadow-lg">
              <Users className="text-white w-5 h-5" />
            </div>
            <h1 className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-700 to-purple-700">
              Users Maintainer
            </h1>
          </div>

          <div className="flex items-center gap-4">
            <span className="text-sm font-medium text-slate-500 hidden sm:block">Admin Mode</span>
            <button className="p-2 rounded-lg hover:bg-slate-100 text-slate-600 transition-colors">
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        </div>
      </header>

      <main className="flex-1 w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white/50 backdrop-blur-lg rounded-2xl shadow-xl border border-white/60 p-6 md:p-8">
          {children}
        </div>
      </main>
    </div>
  );
};
