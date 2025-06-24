import { Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import DRPage from './pages/DRPage'
import LungPage from './pages/LungPage'
import ResultPage from './pages/ResultPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ProtectedRoute from './components/ProtectedRoute'
import OAuthRedirectPage from './pages/OAuthRedirectPage'
import { useAuth } from './context/AuthContext'


function App() {
  const { user } = useAuth();
  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <Routes>
        <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
        <Route path="/dr" element={<ProtectedRoute><DRPage /></ProtectedRoute>} />
        <Route path="/lung" element={<ProtectedRoute><LungPage /></ProtectedRoute>} />
        <Route path="/result" element={<ProtectedRoute><ResultPage /></ProtectedRoute>} />

        <Route path="/register" element={!user ? <RegisterPage /> : <Navigate to="/" />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/oauth2/redirect" element={<OAuthRedirectPage />} />
      </Routes>
    </div>
  )
}

export default App
