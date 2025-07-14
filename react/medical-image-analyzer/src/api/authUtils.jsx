export const getAuth = () => ({
    accessToken: localStorage.getItem('accessToken'),
    refreshToken: localStorage.getItem('refreshToken')
})

export const setAccessToken = (token) => {
    localStorage.setItem('accessToken', token)
}

export const clearAuth = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
}
