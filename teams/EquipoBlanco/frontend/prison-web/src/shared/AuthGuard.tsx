import { AuthProvider } from './authContext'

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  return <AuthProvider>{children}</AuthProvider>
}
