import React, { useState } from 'react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

function App() {
  const [curriculumFiles, setCurriculumFiles] = useState([]);
  const [resumeFiles, setResumeFiles] = useState([]);
  const [userPrompt, setUserPrompt] = useState('');
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState('');
  const [thoughts, setThoughts] = useState([]);
  const [error, setError] = useState(null);

  const handleAnalyze = async () => {
    if (curriculumFiles.length === 0 || resumeFiles.length === 0) {
      alert("Please upload at least one curriculum and one resume file.");
      return;
    }

    setLoading(true);
    setError(null);
    setResults('');
    setThoughts([]);

    const formData = new FormData();
    for (let i = 0; i < curriculumFiles.length; i++) {
      formData.append('curriculum', curriculumFiles[i]);
    }
    for (let i = 0; i < resumeFiles.length; i++) {
      formData.append('resume', resumeFiles[i]);
    }
    
    if (userPrompt) {
      formData.append('prompt', userPrompt);
    }

    try {
      const response = await fetch('http://localhost:8080/api/agent/analyze', {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`Server responded with ${response.status}: ${response.statusText}`);
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { value, done } = await reader.read();
        if (done) break;

        buffer += decoder.decode(value, { stream: true });
        
        // SSE events are separated by double newlines
        const parts = buffer.split('\n\n');
        buffer = parts.pop(); // Keep the last potentially incomplete event in the buffer

        for (const part of parts) {
          const lines = part.split('\n');
          let eventType = '';
          let dataLines = [];

          for (const line of lines) {
            if (line.startsWith('event:')) {
              eventType = line.substring(6).trim();
            } else if (line.startsWith('data:')) {
              dataLines.push(line.substring(5).trim());
            }
          }

          if (eventType && dataLines.length > 0) {
            const combinedData = dataLines.join('\n');
            processEvent(eventType, combinedData);
          }
        }
      }
    } catch (err) {
      console.error(err);
      setError(`Error: ${err.message}`);
      setLoading(false);
    }
  };

  const processEvent = (type, data) => {
    try {
      switch (type) {
        case 'decision':
          setThoughts(prev => [...prev, { type: 'decision', content: data }]);
          break;
        case 'actionResult':
          setThoughts(prev => [...prev, { type: 'actionResult', content: JSON.parse(data) }]);
          break;
        case 'complete':
          const parsedData = JSON.parse(data);
          const combinedResults = parsedData.results.join('\n\n---\n\n');
          setResults(combinedResults);
          setLoading(false);
          break;
        case 'error':
          setError(`Agent Error: ${data}`);
          setLoading(false);
          break;
        default:
          break;
      }
    } catch (e) {
      console.error("Error parsing event data", e);
    }
  };

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <h1>Multi-Agent Learning Assistant</h1>
      </header>

      <div style={styles.main}>
        {/* Left Section: Uploads and Chat */}
        <div style={styles.leftPanel}>
          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>1. Curriculum Documents</h3>
            <p style={styles.sectionDesc}>Upload one or more files (PDF, Word, Image)</p>
            <input 
              type="file" 
              accept="application/pdf,image/*,.doc,.docx" 
              multiple 
              onChange={(e) => setCurriculumFiles(Array.from(e.target.files))}
              style={styles.fileInput}
            />
            {curriculumFiles.length > 0 && (
              <ul style={styles.fileList}>
                {curriculumFiles.map((file, i) => <li key={i}>{file.name}</li>)}
              </ul>
            )}
          </div>

          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>2. Resume Documents</h3>
            <p style={styles.sectionDesc}>Upload your resume(s)</p>
            <input 
              type="file" 
              accept="application/pdf,image/*,.doc,.docx" 
              multiple 
              onChange={(e) => setResumeFiles(Array.from(e.target.files))}
              style={styles.fileInput}
            />
            {resumeFiles.length > 0 && (
              <ul style={styles.fileList}>
                {resumeFiles.map((file, i) => <li key={i}>{file.name}</li>)}
              </ul>
            )}
          </div>

          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>3. Custom Instructions</h3>
            <textarea 
              placeholder="E.g., Focus on my Python skills, or tailor the SOP for a Data Science master's."
              value={userPrompt}
              onChange={(e) => setUserPrompt(e.target.value)}
              style={styles.textArea}
            />
          </div>

          <button 
            onClick={handleAnalyze} 
            disabled={loading}
            style={{
              ...styles.analyzeButton,
              backgroundColor: loading ? '#ccc' : '#007bff'
            }}
          >
            {loading ? 'Thinking...' : 'Start Agent Analysis'}
          </button>

          {error && <div style={styles.errorBox}>{error}</div>}
        </div>

        {/* Right Section: Markdown Results and Progress */}
        <div style={styles.rightPanel}>
          <div style={styles.resultsHeader}>
            <h2 style={styles.resultsTitle}>Analysis Output</h2>
            {loading && <div style={styles.loadingBadge}>Agent is Active</div>}
          </div>
          
          <div style={styles.resultsContent}>
            <div style={styles.markdownContainer}>
              {results ? (
                <ReactMarkdown remarkPlugins={[remarkGfm]}>
                  {results}
                </ReactMarkdown>
              ) : (
                <div style={styles.emptyState}>
                  {loading 
                    ? 'The agent is analyzing your files and generating results. Monitor progress below.' 
                    : 'Results will appear here once the analysis is complete.'}
                </div>
              )}
            </div>

            {/* Bottom Progress Section */}
            <div style={styles.progressPanel}>
              <h4 style={styles.progressTitle}>🤖 Live Agent Progress</h4>
              <div style={styles.thoughtsScroll}>
                {thoughts.length === 0 && !loading && (
                  <div style={styles.noProgress}>No active processes</div>
                )}
                {thoughts.map((thought, i) => (
                  <div key={i} style={styles.thoughtItem}>
                    <span style={styles.thoughtType}>
                      {thought.type === 'decision' ? '👉 Plan:' : '✅ Result:'}
                    </span>
                    <span style={styles.thoughtContent}>
                      {thought.type === 'decision' 
                        ? thought.content 
                        : typeof thought.content === 'object' 
                          ? `Completed ${thought.content.tool || 'action'}`
                          : thought.content}
                    </span>
                  </div>
                ))}
                {loading && <div style={styles.spinner}>The agent is thinking...</div>}
                <div ref={(el) => { if (el) el.scrollIntoView({ behavior: 'smooth' }); }} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    height: '100vh',
    fontFamily: '"Segoe UI", Tahoma, Geneva, Verdana, sans-serif',
    backgroundColor: '#f8f9fa',
    color: '#333',
  },
  header: {
    padding: '10px 30px',
    backgroundColor: '#343a40',
    color: '#fff',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  main: {
    display: 'flex',
    flex: 1,
    overflow: 'hidden',
    padding: '20px',
    gap: '20px',
  },
  leftPanel: {
    flex: '0 0 350px',
    backgroundColor: '#fff',
    padding: '20px',
    borderRadius: '8px',
    boxShadow: '0 4px 6px rgba(0,0,0,0.05)',
    display: 'flex',
    flexDirection: 'column',
    overflowY: 'auto',
  },
  rightPanel: {
    flex: 1,
    backgroundColor: '#fff',
    padding: '20px',
    borderRadius: '8px',
    boxShadow: '0 4px 6px rgba(0,0,0,0.05)',
    display: 'flex',
    flexDirection: 'column',
    overflow: 'hidden',
  },
  resultsHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '10px',
    paddingBottom: '10px',
    borderBottom: '2px solid #f0f0f0',
  },
  loadingBadge: {
    backgroundColor: '#e7f3ff',
    color: '#007bff',
    padding: '4px 12px',
    borderRadius: '20px',
    fontSize: '0.8rem',
    fontWeight: 'bold',
    animation: 'pulse 2s infinite',
  },
  resultsContent: {
    display: 'flex',
    flexDirection: 'column',
    flex: 1,
    overflow: 'hidden',
  },
  section: {
    marginBottom: '20px',
  },
  sectionTitle: {
    margin: '0 0 5px 0',
    fontSize: '1rem',
    color: '#0056b3',
  },
  sectionDesc: {
    margin: '0 0 10px 0',
    fontSize: '0.8rem',
    color: '#666',
  },
  fileInput: {
    width: '100%',
    padding: '8px 0',
  },
  fileList: {
    fontSize: '0.8rem',
    color: '#555',
    marginTop: '8px',
    paddingLeft: '15px',
  },
  textArea: {
    width: '100%',
    height: '80px',
    padding: '10px',
    borderRadius: '5px',
    border: '1px solid #ddd',
    fontSize: '0.85rem',
    resize: 'none',
    boxSizing: 'border-box',
  },
  analyzeButton: {
    padding: '12px',
    fontSize: '1rem',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
    transition: 'background-color 0.2s',
    fontWeight: 'bold',
    marginTop: '10px',
  },
  errorBox: {
    marginTop: '15px',
    padding: '10px',
    backgroundColor: '#f8d7da',
    color: '#721c24',
    borderRadius: '4px',
    fontSize: '0.8rem',
  },
  resultsTitle: {
    margin: 0,
    fontSize: '1.4rem',
    color: '#333',
  },
  markdownContainer: {
    flex: 1,
    overflowY: 'auto',
    padding: '10px',
    lineHeight: '1.6',
    fontSize: '1rem',
    backgroundColor: '#fff',
  },
  progressPanel: {
    height: '200px',
    borderTop: '1px solid #eee',
    backgroundColor: '#fcfcfc',
    display: 'flex',
    flexDirection: 'column',
    padding: '10px',
  },
  progressTitle: {
    margin: '0 0 8px 0',
    fontSize: '0.9rem',
    color: '#555',
    textTransform: 'uppercase',
    letterSpacing: '0.5px',
  },
  thoughtsScroll: {
    flex: 1,
    overflowY: 'auto',
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
    padding: '5px',
  },
  thoughtItem: {
    padding: '8px 12px',
    backgroundColor: '#fff',
    borderRadius: '4px',
    borderLeft: '3px solid #007bff',
    boxShadow: '0 1px 2px rgba(0,0,0,0.03)',
    fontSize: '0.85rem',
    display: 'flex',
    gap: '8px',
  },
  thoughtType: {
    fontWeight: 'bold',
    color: '#007bff',
    whiteSpace: 'nowrap',
  },
  thoughtContent: {
    color: '#444',
  },
  noProgress: {
    textAlign: 'center',
    color: '#999',
    fontSize: '0.85rem',
    marginTop: '20px',
    fontStyle: 'italic',
  },
  emptyState: {
    display: 'flex',
    height: '100%',
    alignItems: 'center',
    justifyContent: 'center',
    color: '#999',
    fontStyle: 'italic',
    textAlign: 'center',
    padding: '0 40px',
  },
  spinner: {
    padding: '10px',
    color: '#007bff',
    fontStyle: 'italic',
    fontSize: '0.85rem',
  }
};

export default App;
