import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import qs from 'query-string'

export default function OAuth2Redirect() {
  const navigate = useNavigate()
  const { login } = useAuth()

  useEffect(() => {
    const { accessToken, refreshToken } = qs.parse(window.location.search)

    if (!accessToken || !refreshToken) {
      alert('OAuth login failed: missing tokens')
      navigate('/login')
      return
    }

    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)

    login({
      user: { email: "someone@example.com", name: "X" },
      accessToken,
      refreshToken
    })

    navigate('/')
  }, [login, navigate])

  return <div className="p-4">Signing you in via Google...</div>
}
