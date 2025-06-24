import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function OAuthRedirectPage() {
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const { login } = useAuth()

  useEffect(() => {
    // const token = params.get('token')
    const username = params.get('username')

    if ( username) {
      login({ username })
      navigate('/dr')
    } else {
      alert("OAuth login failed.")
      navigate('/login')
    }
  }, [])

  return <p className="text-center mt-10">Logging you in...</p>
}
