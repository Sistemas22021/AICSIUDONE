'use client'

import { useState } from 'react'
import { CustodiaSidebar } from '@/components/custodia-sidebar'
import { CustodiaHeader } from '@/components/custodia-header'
import { IdentityPanel } from '@/components/identity-panel'
import { TranscriptionPanel } from '@/components/transcription-panel'
import { AnalysisPanel } from '@/components/analysis-panel'
import { AuditTrailPanel } from '@/components/audit-trail-panel'
import { GuardOfficerStepper } from '@/components/guard-officer-stepper'

export default function Home() {
  const [activeView, setActiveView] = useState('interrogation')
  const [currentStep, setCurrentStep] = useState(1)

  const renderMainContent = () => {
    switch (activeView) {
      case 'dashboard':
        return (
          <div className="grid grid-cols-12 gap-6 h-full">
            <div className="col-span-3 overflow-y-auto">
              <IdentityPanel />
            </div>
            <div className="col-span-6 overflow-hidden">
              <TranscriptionPanel />
            </div>
            <div className="col-span-3 overflow-y-auto">
              <AnalysisPanel />
            </div>
          </div>
        )
      
      case 'guard-officer':
        return (
          <div className="h-full">
            <GuardOfficerStepper />
          </div>
        )
      
      case 'interrogation':
        return (
          <div className="grid grid-cols-12 gap-6 h-full">
            <div className="col-span-3 overflow-y-auto">
              <IdentityPanel />
            </div>
            <div className="col-span-6 overflow-hidden">
              <TranscriptionPanel />
            </div>
            <div className="col-span-3 overflow-y-auto">
              <AnalysisPanel />
            </div>
          </div>
        )
      
      case 'audit':
        return (
          <div className="h-full">
            <AuditTrailPanel />
          </div>
        )
      
      case 'settings':
        return (
          <div className="flex items-center justify-center h-full">
            <div className="text-center">
              <h3 className="text-2xl font-bold text-foreground mb-2">Configuración del Sistema</h3>
              <p className="text-muted-foreground">Módulo en desarrollo</p>
            </div>
          </div>
        )
      
      default:
        return null
    }
  }

  return (
    <div className="flex h-screen bg-background">
      {/* Sidebar */}
      <CustodiaSidebar activeView={activeView} onViewChange={setActiveView} />

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <CustodiaHeader currentView={activeView} currentStep={activeView === 'guard-officer' ? currentStep : undefined} />

        {/* Main Content Area */}
        <main className="flex-1 overflow-hidden bg-background">
          {renderMainContent()}
        </main>
      </div>
    </div>
  )
}
