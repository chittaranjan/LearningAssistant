import React, { useState } from 'react';
import axios from 'axios';

function App() {
  const [curriculumFiles, setCurriculumFiles] = useState([]);
  const [resumeFiles, setResumeFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState([]);
  const [error, setError] = useState(null);

  const handleAnalyze = async () => {
    if (curriculumFiles.length === 0 || resumeFiles.length === 0) {
      alert("Please upload at least one curriculum and one resume file.");
      return;
    }

    setLoading(true);
    setError(null);
    setResults([]);

    const formData = new FormData();
    for (let i = 0; i < curriculumFiles.length; i++) {
      formData.append('curriculum', curriculumFiles[i]);
    }
    for (let i = 0; i < resumeFiles.length; i++) {
      formData.append('resume', resumeFiles[i]);
    }

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
        <h3>Upload Curriculum (PDF, Image, Word) - Multiple allowed</h3>
        <input 
          type="file" 
          accept="application/pdf,image/*,.doc,.docx" 
          multiple 
          onChange={(e) => setCurriculumFiles(Array.from(e.target.files))} 
        />
        {curriculumFiles.length > 0 && (
          <ul>
            {curriculumFiles.map((file, i) => <li key={i}>{file.name}</li>)}
          </ul>
        )}
      </div>

      <div style={{ marginBottom: '20px' }}>
        <h3>Upload Resume (PDF, Image, Word) - Multiple allowed</h3>
        <input 
          type="file" 
          accept="application/pdf,image/*,.doc,.docx" 
          multiple 
          onChange={(e) => setResumeFiles(Array.from(e.target.files))} 
        />
        {resumeFiles.length > 0 && (
          <ul>
            {resumeFiles.map((file, i) => <li key={i}>{file.name}</li>)}
          </ul>
        )}
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
