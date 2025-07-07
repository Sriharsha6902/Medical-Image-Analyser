import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import axios from 'axios'
import Loader from '../components/Loader'
import Modal from '../components/Modal'

export default function LoginPage() {

  const [showModal, setShowModal] = useState(false)
  const [loading, setLoading] = useState(false)
  const [userCred, setUserCred] = useState('')
  const [password, setPassword] = useState('')
  const { login, user } = useAuth()
  const navigate = useNavigate()
  useEffect(() => {
    if (!showModal) return;

    const timer = setTimeout(() => setShowModal(false), 3000)
    return () => clearTimeout(timer)
  }, [showModal])

  const handleSubmit = async (e) => {
    if(user) navigate('/')
    e.preventDefault()
    setLoading(true)
    try {
        const res = await axios.post(
        'http://localhost:8000/auth/login',
          {
            userCred,
            password,
            role: 'USER',
            authProvider: 'LOCAL',
          },
          {
            headers: {
              'Content-Type': 'application/json',
            },
            withCredentials: false
          }
        )
        
        login(res.data)
        navigate('/')
    } catch (err) {
        console.error('Login failed:', err);
        setShowModal(true)
    } finally {
        setLoading(false)
    }
  }

  return (
    <>
      {showModal && (
      <Modal
        message="Incorrect username or password."
        title="Invalid credentials"
        onClose={() => setShowModal(false)}
      />
    )}
    <form onSubmit={handleSubmit} className="p-6 max-w-md mx-auto">
      <h2 className="text-2xl font-bold mb-4">Login</h2>
      <input
        type="text"
        placeholder="Username"
        className="w-full mb-3 p-2 border rounded"
        value={userCred}
        onChange={(e) => setUserCred(e.target.value)}
      />
      <input
        type="password"
        placeholder="Password"
        className="w-full mb-3 p-2 border rounded"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
        <button
        type="submit"
        className="w-full bg-blue-600 text-white p-2 rounded flex justify-center items-center gap-2"
        >
        Login
        {loading && <Loader />}
        </button>
        <br/>
        <p>You can also sign in with</p>
        <br/>
        <button
          className="w-full bg-red-600 text-white p-2 rounded mb-4"
          onClick={() => {
            window.location.href = 'http://localhost:8000/oauth2/authorization/google'
          }}
        >
          Google
        </button>
    </form>
    </>
  )
}
