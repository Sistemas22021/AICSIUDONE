'use client'

import { useState } from 'react'
import { CustodiaSidebar } from '@/components/custodia-sidebar'
import { CustodiaHeader } from '@/components/custodia-header'
import { TranscriptionPanel } from '@/components/transcription-panel'

export default function Home() {
  const [activeView, setActiveView] = useState('interrogation')

  const renderMainContent = () => {
    switch (activeView) {
      case 'interrogation':
        return (
          <div className="flex items-center justify-center h-full p-6">
            <div className="w-full max-w-4xl h-full overflow-hidden">
              <TranscriptionPanel />
            </div>
          </div>
        )
      default:
        return null
    }
  }

  return (
    <div className="flex flex-col md:flex-row h-[100dvh] bg-background">
      {/* Sidebar */}
      <div className="hidden md:block">
        <CustodiaSidebar activeView={activeView} onViewChange={setActiveView} />
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Header */}
        <CustodiaHeader currentView={activeView} />

        {/* Main Content Area */}
        <main className="flex-1 overflow-hidden bg-background">
          {renderMainContent()}
        </main>
      </div>
    </div>
  )
}
