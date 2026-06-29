import React, { useState } from 'react';

export default function ProjectSidebar({ projects, selected, onSelect, onAdd }) {
  const [adding, setAdding] = useState(false);
  const [name, setName]     = useState('');

  const handleAdd = () => {
    if (!name.trim()) return;
    onAdd(name.trim());
    setName('');
    setAdding(false);
  };

  return (
    <aside className="sidebar">
      <h3>Projects</h3>

      <ul className="project-list">
        <li
          className={`project-item ${!selected ? 'active' : ''}`}
          onClick={() => onSelect('')}
        >
          All Tasks
        </li>
        {projects.map((p) => (
          <li
            key={p.id}
            className={`project-item ${String(selected) === String(p.id) ? 'active' : ''}`}
            onClick={() => onSelect(p.id)}
          >
            {p.name}
            <span className="project-count">{p.taskCount}</span>
          </li>
        ))}
      </ul>

      {adding ? (
        <div className="add-project">
          <input
            autoFocus
            value={name}
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => { if (e.key === 'Enter') handleAdd(); if (e.key === 'Escape') setAdding(false); }}
            placeholder="Project name"
          />
          <button className="btn btn-primary" onClick={handleAdd}>Add</button>
          <button className="btn" onClick={() => setAdding(false)}>Cancel</button>
        </div>
      ) : (
        <button className="btn add-project-btn" onClick={() => setAdding(true)}>+ New Project</button>
      )}
    </aside>
  );
}
