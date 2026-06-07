import React, { useState } from 'react';
import axios from 'axios';

function App() {
  const [curriculum, setCurriculum] = useState(null);
  const [resume, setResume] = useState(null);
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState([]);
  const [error, setError] = useState(null);

  const handleAnalyze = async () => {
    if (!curriculum || !resume) {
      alert("Please upload both curriculum and resume PDFs.");
      return;
    }

    setLoading(true);
    setError(null);
    setResults([]);

    const formData = new FormData();
    formData.append('curriculum', curriculum);
    formData.append('resume', resume);

    try {
      const response = await axios.post('http://localhost:8080/api/agent/analyze', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      setResults(response.data.results);
    } catch (err) {
      console.error(err);
      setError("Failed to analyze files. Make sure the backend is running.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>Multi-Agent Curriculum & Resume Analyzer</h1>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Upload Curriculum (PDF)</h3>
        <input type="file" accept="application/pdf" onChange={(e) => setCurriculum(e.target.files[0])} />
      </div>

      <div style={{ marginBottom: '20px' }}>
        <h3>Upload Resume (PDF)</h3>
        <input type="file" accept="application/pdf" onChange={(e) => setResume(e.target.files[0])} />
      </div>

      <button 
        onClick={handleAnalyze} 
        disabled={loading}
        style={{ padding: '10px 20px', fontSize: '16px', cursor: 'pointer' }}
      >
        {loading ? 'Analyzing...' : 'Start Analysis'}
      </button>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <div style={{ marginTop: '40px' }}>
        <h2>Analysis Results</h2>
        {results.length === 0 && !loading && <p>No results yet. Upload files and click Analyze.</p>}
        {results.map((res, index) => (
          <div key={index} style={{ 
            backgroundColor: '#f4f4f4', 
            padding: '15px', 
            marginBottom: '10px', 
            borderRadius: '5px',
            whiteSpace: 'pre-wrap'
          }}>
            {res}
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;
