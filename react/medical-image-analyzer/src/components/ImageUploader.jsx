import { useState } from 'react'

export default function ImageUploader({ onFileSelect }) {
  const [preview, setPreview] = useState(null)
  const [error, setError] = useState('')

  const handleFileChange = (e) => {
    const file = e.target.files[0]
    if (!file) return

    if (!file.type.startsWith('image/')) {
      setError('Only image files are allowed.')
      return
    }

    if (file.size > 5 * 1024 * 1024) { // 5 MB limit
      setError('File too large (max 5MB).')
      return
    }

    setError('')
    setPreview(URL.createObjectURL(file))
    onFileSelect(file)
  }

  return (
    <div className="p-4 border border-dashed border-gray-400 rounded-md w-full max-w-md mx-auto">
      <input type="file" accept="image/*" onChange={handleFileChange} />
      {error && <p className="text-red-500 mt-2">{error}</p>}
      {preview && (
        <img src={preview} alt="Preview" className="mt-4 max-h-64 rounded shadow" />
      )}
    </div>
  )
}
