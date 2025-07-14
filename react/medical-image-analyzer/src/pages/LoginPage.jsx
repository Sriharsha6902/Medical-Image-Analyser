import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import axios from 'axios'
import Loader from '../components/Loader'
import Modal from '../components/Modal'

export default function LoginPage() {
    const [showModal, setShowModal] = useState(false)
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)
    const [userCred, setUserCred] = useState('')
    const [password, setPassword] = useState('')
    const { login, user } = useAuth()
    const navigate = useNavigate()

    useEffect(() => {
        if (!showModal) return
        const timer = setTimeout(() => setShowModal(false), 3000)
        return () => clearTimeout(timer)
    }, [showModal])

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (user) navigate('/')
        setLoading(true)

        try {
            const res = await axios.post(
                'http://localhost:8000/auth/login',
                { userCred, password },
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: false
                }
            )

            const { accessToken, refreshToken, user: userObj } = res.data
            login({ accessToken, refreshToken, user: userObj })

            navigate('/')
        } catch (err) {
            setError(err)
            console.error('Login failed:', err)
            setShowModal(true)
        } finally {
            setLoading(false)
        }
    }

    const handleOAuthLogin = () => {
        window.location.href = 'http://localhost:8000/oauth2/authorization/google'
    }

    return (
        <>
            {showModal && (
                <Modal
                    message={error.response.data.error}
                    title="Login Failed."
                    onClose={() => setShowModal(false)}
                />
            )}

            <form onSubmit={handleSubmit} className="p-6 max-w-md mx-auto">
                <h2 className="text-2xl font-bold mb-4">Login</h2>
                <input
                    type="text"
                    placeholder="Username or Email"
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
                <div className="text-center my-4">or</div>
                <button
                    type="button"
                    className="w-full bg-red-600 text-white p-2 rounded"
                    onClick={handleOAuthLogin}
                >
                    Sign in with Google
                </button>
            </form>
        </>
    )
}
