import { useState, useRef } from 'react'

export default function ImageUploader({ onFileSelect }) {
  const [preview, setPreview] = useState(null)
  const [error, setError] = useState('')
  const fileInputRef = useRef(null)

  const handleFileChange = (e) => {
    const file = e.target.files[0]
    if (!file) return

    if (!file.type.startsWith('image/')) {
      setError('Only image files are allowed.')
      return
    }

    if (file.size > 5 * 1024 * 1024) {
      setError('File too large (max 5MB).')
      return
    }

    setError('')
    setPreview(URL.createObjectURL(file))
    onFileSelect(file)
  }

  const handleFileRemoval = () => {
    setPreview(null)
    setError('')
    onFileSelect(null)

    if (fileInputRef.current) {
      fileInputRef.current.value = null
    }
  }

  return (
      <div className="p-4 border border-dashed border-gray-400 rounded-md w-full max-w-md mx-auto">
        <input
            type="file"
            accept="image/*"
            onChange={handleFileChange}
            ref={fileInputRef}
        />

        {error && <p className="text-red-500 mt-2">{error}</p>}

        {preview && (
            <>
              <img src={preview} alt="Preview" className="mt-4 max-h-64 rounded shadow" />
              <button
                  onClick={handleFileRemoval}
                  className="mt-2 text-sm text-red-600 hover:underline"
              >
                Remove
              </button>
            </>
        )}
      </div>
  )
}
