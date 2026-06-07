import React from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

const AnalysisOutput = ({ results, loading }) => {
  return (
    <div style={styles.resultsPanel}>
      <div style={styles.resultsHeader}>
        <h2 style={styles.resultsTitle}>Analysis Output</h2>
        {loading && <div style={styles.loadingBadge}>Agent is Active</div>}
      </div>
      
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
    </div>
  );
};

const styles = {
  resultsPanel: {
    flex: 1,
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
  resultsTitle: {
    margin: 0,
    fontSize: '1.4rem',
    color: '#333',
  },
  loadingBadge: {
    backgroundColor: '#e7f3ff',
    color: '#007bff',
    padding: '4px 12px',
    borderRadius: '20px',
    fontSize: '0.8rem',
    fontWeight: 'bold',
  },
  markdownContainer: {
    flex: 1,
    overflowY: 'auto',
    padding: '10px',
    lineHeight: '1.6',
    fontSize: '1rem',
    backgroundColor: '#fff',
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
};

export default AnalysisOutput;
