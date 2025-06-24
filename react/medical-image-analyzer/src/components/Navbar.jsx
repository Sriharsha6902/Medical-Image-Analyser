import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="bg-white shadow p-4 flex justify-between items-center">
      <div className="flex gap-6">
        <Link to="/" className="text-blue-600 font-bold">MedImgAnalyser</Link>

        {user && (
          <>
            <Link to="/dr">DR Detection</Link>
            <Link to="/lung">Pneumonia Detection</Link>
            {/* <Link to="/result">Results</Link> */}
          </>
        )}
      </div>

      <div className="flex gap-4 items-center">
        {!user ? (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        ) : (
          <>
            <span className="text-sm text-gray-700">👤 {user.username}</span>
            <button
              onClick={handleLogout}
              className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
            >
              Logout
            </button>
          </>
        )}
      </div>
    </nav>
  )
}
