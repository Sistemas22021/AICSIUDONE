import { type FC } from 'react'
import SidebarLayout from './SidebarLayout'

export default function PlaceholderPage({ title, description, icon: Icon }: { title: string; description: string; icon: FC<{ className?: string }> }) {
  return (
    <SidebarLayout>
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
        <div className="bg-gray-100 p-6 rounded-full mb-6">
          {Icon ? <Icon className="w-12 h-12 text-gray-400" /> : (
            <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
          )}
        </div>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">{title}</h2>
        <p className="text-gray-500 max-w-md">{description}</p>
        <div className="mt-8 flex items-center gap-2 px-4 py-2 bg-blue-50 border border-blue-200 rounded-lg text-sm text-blue-700">
          <span>M\u00f3dulo en desarrollo — Pr\u00f3ximamente disponible</span>
        </div>
      </div>
    </SidebarLayout>
  )
}
