import { createContext, useContext, useState, useEffect } from 'react'
import axios from "axios";

const AuthContext = createContext()

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [accessToken, setAccessToken] = useState(null)
  const [refreshToken, setRefreshToken] = useState(null)

  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    const storedAccessToken = localStorage.getItem('accessToken')
    const storedRefreshToken = localStorage.getItem('refreshToken')

    if (storedUser) setUser(JSON.parse(storedUser))
    if (storedAccessToken) setAccessToken(storedAccessToken)
    if (storedRefreshToken) setRefreshToken(storedRefreshToken)
  }, [])

  const login = ({ user, accessToken, refreshToken }) => {
    setUser(user)
    setAccessToken(accessToken)
    setRefreshToken(refreshToken)

    localStorage.setItem('user', JSON.stringify(user))
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)

    console.log('User logged in:', user)
  }

  const logout = async () => {
    await axios.post('http://localhost:8000/auth/logout-session', {refreshToken})
    setUser(null)
    setAccessToken(null)
    setRefreshToken(null)

    localStorage.removeItem('user')
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  return (
      <AuthContext.Provider value={{ user, accessToken, refreshToken, login, logout }}>
        {children}
      </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
