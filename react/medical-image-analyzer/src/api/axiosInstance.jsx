import axios from 'axios'
import { getAuth, clearAuth, setAccessToken } from './authUtils'

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8000',
    withCredentials: true
})

axiosInstance.interceptors.request.use(
    (config) => {
        const { accessToken } = getAuth()
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true

            try {
                const { refreshToken } = getAuth()
                const res = await axios.post('http://localhost:8000/auth/refresh', { refreshToken })

                setAccessToken(res.data.accessToken)

                originalRequest.headers.Authorization = `Bearer ${res.data.accessToken}`
                return axiosInstance(originalRequest)

            } catch (refreshError) {
                clearAuth()
                window.location.href = '/login'
                return Promise.reject(refreshError)
            }
        }

        return Promise.reject(error)
    }
)

export default axiosInstance
