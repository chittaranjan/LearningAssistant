import React, { useRef, useEffect } from 'react';

const AgentProgress = ({ thoughts, loading }) => {
  const scrollRef = useRef(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [thoughts, loading]);

  return (
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
              {thought.content}
            </span>
          </div>
        ))}
        {loading && <div style={styles.spinner}>The agent is thinking...</div>}
        <div ref={scrollRef} />
      </div>
    </div>
  );
};

const styles = {
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
  spinner: {
    padding: '10px',
    color: '#007bff',
    fontStyle: 'italic',
    fontSize: '0.85rem',
  }
};

export default AgentProgress;
