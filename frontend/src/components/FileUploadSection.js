import React from 'react';

const FileUploadSection = ({ title, description, accept, files, onFileChange }) => {
  return (
    <div style={styles.section}>
      <h3 style={styles.sectionTitle}>{title}</h3>
      <p style={styles.sectionDesc}>{description}</p>
      <input 
        type="file" 
        accept={accept} 
        multiple 
        onChange={(e) => onFileChange(Array.from(e.target.files))}
        style={styles.fileInput}
      />
      {files.length > 0 && (
        <ul style={styles.fileList}>
          {files.map((file, i) => <li key={i}>{file.name}</li>)}
        </ul>
      )}
    </div>
  );
};

const styles = {
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
};

export default FileUploadSection;
