export default function Modal({ title, message, onClose }) {
  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center">
      <div className="bg-white p-4 rounded shadow-lg max-w-sm w-full text-center">
        <h2 className="text-lg font-semibold mb-3">{title}</h2>
        <p className="text-gray-700 mb-5">{message}</p>
        <button
          onClick={onClose}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Close
        </button>
      </div>
    </div>
  )
}
