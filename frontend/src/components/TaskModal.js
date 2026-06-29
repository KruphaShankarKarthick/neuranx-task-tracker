import React, { useState, useEffect } from 'react';

const EMPTY = {
  title: '', description: '', status: 'TODO',
  priority: 'MEDIUM', dueDate: '', projectId: ''
};

export default function TaskModal({ task, projects, onSave, onClose }) {
  const [form, setForm] = useState(EMPTY);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (task) {
      setForm({
        title:       task.title || '',
        description: task.description || '',
        status:      task.status || 'TODO',
        priority:    task.priority || 'MEDIUM',
        dueDate:     task.dueDate || '',
        projectId:   task.projectId || '',
      });
    } else {
      setForm(EMPTY);
    }
    setErrors({});
  }, [task]);

  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  const validate = () => {
    const errs = {};
    if (!form.title.trim()) errs.title = 'Title is required';
    if (form.title.length > 255) errs.title = 'Max 255 characters';
    return errs;
  };

  const handleSubmit = () => {
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    onSave({
      title:       form.title.trim(),
      description: form.description || null,
      status:      form.status || 'TODO',
      priority:    form.priority || 'MEDIUM',
      dueDate:     form.dueDate || null,
      projectId:   form.projectId ? Number(form.projectId) : null,
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>{task ? 'Edit Task' : 'New Task'}</h2>

        <label>Title *
          <input value={form.title} onChange={set('title')} placeholder="Task title" />
          {errors.title && <span className="field-error">{errors.title}</span>}
        </label>

        <label>Description
          <textarea value={form.description} onChange={set('description')} rows={3} placeholder="Optional description" />
        </label>

        <div className="row-2">
          <label>Status
            <select value={form.status} onChange={set('status')}>
              {['TODO', 'DOING', 'DONE'].map((s) => <option key={s}>{s}</option>)}
            </select>
          </label>

          <label>Priority
            <select value={form.priority} onChange={set('priority')}>
              {['LOW', 'MEDIUM', 'HIGH'].map((p) => <option key={p}>{p}</option>)}
            </select>
          </label>
        </div>

        <div className="row-2">
          <label>Due Date
            <input type="date" value={form.dueDate} onChange={set('dueDate')} />
          </label>

          <label>Project
            <select value={form.projectId} onChange={set('projectId')}>
              <option value="">— None —</option>
              {projects.map((p) => (
                <option key={p.id} value={p.id}>{p.name}</option>
              ))}
            </select>
          </label>
        </div>

        <div className="modal-actions">
          <button className="btn" onClick={onClose}>Cancel</button>
          <button className="btn btn-primary" onClick={handleSubmit}>
            {task ? 'Save Changes' : 'Create Task'}
          </button>
        </div>
      </div>
    </div>
  );
}
