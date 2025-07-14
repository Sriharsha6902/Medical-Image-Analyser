import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import Modal from "../components/Modal.jsx";

export default function RegisterPage() {
    const [form, setForm] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    firstName: '',
    lastName: '',
    })
    const [showModal,setShowModal] = useState(false)


    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()
    const [message,setMessage] = useState("Incorrect Details");
    const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true)

        if(form.username === '' || form.firstName === '' || form.lastName === '' || form.email===''){
            alert("Please fill out the details")
            setLoading(false)
            return
        }

        if(form.password===''){
            alert("Please set your password")
            setLoading(false)
            return
        }

        if (form.password !== form.confirmPassword) {
            alert("Passwords do not match")
            setLoading(false)
            return
        }

        try {
            await axios.post('http://localhost:8000/auth/register', {
                ...form,
                role: 'USER',
                authProvider: 'LOCAL',
                }, {
                    withCredentials:false,
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            alert("Successfully Registered")
            navigate('/login')
        } catch (err) {
            setMessage(err.response.data.response)
            setShowModal(true)
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    return (
        <>
            {showModal && (
                <Modal
                    message={message}
                    title="Registration Failed."
                    onClose={() => setShowModal(false)}
                />
            )}
            <form onSubmit={handleSubmit} className="p-6 max-w-md mx-auto">
                <h2 className="text-2xl font-bold mb-4">Register</h2>
                <input name="firstName" placeholder="First Name" className="w-full mb-3 p-2 border rounded" value={form.firstName} onChange={handleChange} />
                <input name="lastName" placeholder="Last Name" className="w-full mb-3 p-2 border rounded" value={form.lastName} onChange={handleChange} />
                <input name="email" placeholder="Email" className="w-full mb-3 p-2 border rounded" value={form.email} onChange={handleChange} />
                <input name="username" placeholder="Username" className="w-full mb-3 p-2 border rounded" value={form.username} onChange={handleChange} />
                <input name="password" type="password" placeholder="Password" className="w-full mb-3 p-2 border rounded" value={form.password} onChange={handleChange} />
                <input name="confirmPassword" type="password" placeholder="Confirm Password" className="w-full mb-3 p-2 border rounded" value={form.confirmPassword} onChange={handleChange}/>
                <button
                type="submit"
                disabled={loading}
                className="w-full bg-green-600 text-white p-2 rounded flex items-center justify-center gap-2"
                >
                Register
                {loading && <span className="animate-spin w-4 h-4 border-2 border-white border-t-transparent rounded-full"></span>}
                </button>
            </form>
        </>
    )
}
