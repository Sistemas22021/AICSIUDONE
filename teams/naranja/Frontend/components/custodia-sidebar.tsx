'use client'

import { LayoutDashboard, FileText, Mic2, Shield, Settings, Circle } from 'lucide-react'
import { cn } from '@/lib/utils'

interface NavItem {
  icon: React.ReactNode
  label: string
  id: string
}

const navItems: NavItem[] = [
  { icon: <Mic2 size={20} />, label: 'Toma de Testimonios', id: 'interrogation' },
]

interface CustodiaSidebarProps {
  activeView: string
  onViewChange: (viewId: string) => void
}

export function CustodiaSidebar({ activeView, onViewChange }: CustodiaSidebarProps) {

  return (
    <aside className="w-64 bg-sidebar border-r border-sidebar-border flex flex-col h-screen">
      {/* Logo Section */}
      <div className="p-6 border-b border-sidebar-border flex flex-col items-center text-center gap-2">
        <div className="w-12 h-12 rounded-xl bg-primary flex items-center justify-center shadow-md shadow-primary/20">
          <Shield size={26} className="text-primary-foreground" />
        </div>
        <div>
          <h1 className="text-xl font-extrabold text-primary tracking-wider leading-none">CUSTODIA</h1>
          <p className="text-[10px] text-sidebar-foreground mt-1 font-mono tracking-widest uppercase opacity-75">360</p>
        </div>
      </div>

      {/* Navigation Menu */}
      <nav className="flex-1 flex flex-col items-center gap-4 px-4 py-6">
        {navItems.map((item) => (
          <button
            key={item.id}
            onClick={() => onViewChange(item.id)}
            className={cn(
              'w-full max-w-[190px] flex flex items-center text-center gap-2 px-4 py-2 rounded-xl transition-all duration-300 font-semibold border border-transparent',
              activeView === item.id
                ? 'bg-primary text-primary-foreground shadow-lg shadow-primary/20 scale-105 border-primary/10'
                : 'text-sidebar-foreground hover:bg-secondary hover:text-primary'
            )}
          >
            <div className={cn(
              'w-10 h-10 rounded-xl flex items-center justify-center transition-colors shadow-sm shrink-0',
              activeView === item.id ? 'bg-primary-foreground/15 text-primary-foreground' : 'bg-secondary text-primary'
            )}>
              {item.icon}
            </div>
            <span className="text-sm tracking-wide">{item.label}</span>
          </button>
        ))}
      </nav>

    </aside>
  )
}
