import { useState } from 'react'
import ImageUploader from '../components/ImageUploader'
import axios from 'axios'
import Loader from '../components/Loader'
import Modal from "../components/Modal.jsx";
import axiosInstance from "../api/axiosInstance.jsx";

export default function DRPage() {
  const [showModal, setShowModal] = useState(false)
  const [modalMsg, setModalMsg] = useState('')
  const [file, setFile] = useState(null)
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleUpload = async () => {
    if (!file) return
    setLoading(true)
    const formData = new FormData()
    formData.append('file', file)

    try {
      const sessionCheck = await  axiosInstance.post('/auth/session-valid')
      if (!sessionCheck.data.valid) throw new Error('Session expired')
      console.log(sessionCheck + "    " + "session valid")
      const response = await axios.post('http://localhost:5000/drdetection', formData)
      setResult(response.data)
    } catch (err) {
      console.error(err)
      setModalMsg(err.message || 'Prediction failed')
      setShowModal(true)
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      {showModal && (
          <Modal
              title="Error Occured"
              message={modalMsg}
              onClose={() => setShowModal(false)}
          />
      )}
      <div className="p-6">
        <h2 className="text-2xl font-semibold mb-4">Diabetic Retinopathy Detection</h2>
        <ImageUploader onFileSelect={setFile} />
        <button
          onClick={handleUpload}
          disabled={!file || loading}
          className="mt-4 bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50"
        >
          {loading ? (
            <div className="flex items-center gap-2">
              <span>Predicting</span>
              <Loader />
            </div>
          ) : (
            'Predict'
          )}
        </button>

        {result && (
          <div className="mt-6 p-4 bg-white shadow rounded">
            <h3 className="text-lg font-bold">Prediction Result:</h3>
            <p>Class: <strong>{result.class}</strong></p>
            <p>Confidence: <strong>{(result.confidence * 100).toFixed(2)}%</strong></p>
          </div>
        )}
      </div>
    </>
  )
}
