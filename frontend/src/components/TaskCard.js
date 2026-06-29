import React from 'react';

const PRIORITY_COLOR = { LOW: '#6b7280', MEDIUM: '#f59e0b', HIGH: '#ef4444' };

export default function TaskCard({ task, onEdit, onDelete, onComplete }) {
  const isOverdue =
    task.dueDate &&
    task.status !== 'DONE' &&
    new Date(task.dueDate) < new Date();

  return (
    <div className={`task-card priority-${task.priority.toLowerCase()}`}>
      <div className="task-header">
        <span
          className="priority-dot"
          style={{ background: PRIORITY_COLOR[task.priority] }}
          title={task.priority}
        />
        <span className="task-title">{task.title}</span>
      </div>

      {task.description && (
        <p className="task-desc">{task.description}</p>
      )}

      <div className="task-meta">
        {task.projectName && (
          <span className="meta-tag project-tag">{task.projectName}</span>
        )}
        {task.dueDate && (
          <span className={`meta-tag ${isOverdue ? 'overdue' : 'due'}`}>
            {isOverdue ? '⚠ ' : '📅 '}
            {new Date(task.dueDate).toLocaleDateString()}
          </span>
        )}
      </div>

      <div className="task-actions">
        {task.status !== 'DONE' && (
          <button className="icon-btn" title="Mark done" onClick={onComplete}>✓</button>
        )}
        <button className="icon-btn" title="Edit" onClick={onEdit}>✏</button>
        <button className="icon-btn danger" title="Delete" onClick={onDelete}>✕</button>
      </div>
    </div>
  );
}
