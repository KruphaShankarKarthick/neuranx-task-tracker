import React, { useState, useEffect, useCallback } from 'react';
import toast, { Toaster } from 'react-hot-toast';
import { getTasks, createTask, updateTask, deleteTask, getProjects, createProject } from './api/client';
import TaskModal from './components/TaskModal';
import TaskCard from './components/TaskCard';
import FilterBar from './components/FilterBar';
import ProjectSidebar from './components/ProjectSidebar';
import './App.css';

const STATUSES = ['TODO', 'DOING', 'DONE'];

export default function App() {
  const [tasks, setTasks]         = useState([]);
  const [projects, setProjects]   = useState([]);
  const [filters, setFilters]     = useState({ status: '', priority: '', projectId: '' });
  const [sortBy, setSortBy]       = useState('dueDate');
  const [sortDir, setSortDir]     = useState('asc');
  const [page, setPage]           = useState(0);
  const [totalPages, setTotal]    = useState(0);
  const [loading, setLoading]     = useState(false);
  const [editTask, setEditTask]   = useState(null);
  const [showModal, setShowModal] = useState(false);

  const fetchTasks = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 12, sortBy, sortDir };
      if (filters.status)    params.status    = filters.status;
      if (filters.priority)  params.priority  = filters.priority;
      if (filters.projectId) params.projectId = filters.projectId;

      const data = await getTasks(params);
      setTasks(data.content);
      setTotal(data.totalPages);
    } catch (err) {
      toast.error('Failed to load tasks');
    } finally {
      setLoading(false);
    }
  }, [page, sortBy, sortDir, filters]);

  const fetchProjects = useCallback(async () => {
    try {
      const data = await getProjects();
      setProjects(data);
    } catch (_) {}
  }, []);

  useEffect(() => { fetchTasks(); }, [fetchTasks]);
  useEffect(() => { fetchProjects(); }, [fetchProjects]);

  const handleSave = async (formData) => {
    try {
      if (editTask) {
        await updateTask(editTask.id, formData);
        toast.success('Task updated');
      } else {
        await createTask(formData);
        toast.success('Task created');
      }
      setShowModal(false);
      setEditTask(null);
      fetchTasks();
    } catch (err) {
      const msg = err.response?.data?.message;
      if (typeof msg === 'object') {
        Object.values(msg).forEach((v) => toast.error(v));
      } else {
        toast.error(msg || 'Save failed');
      }
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this task?')) return;
    try {
      await deleteTask(id);
      toast.success('Task deleted');
      fetchTasks();
    } catch (_) {
      toast.error('Delete failed');
    }
  };

  const handleComplete = async (task) => {
    try {
      await updateTask(task.id, { ...task, status: 'DONE', projectId: task.projectId || null });
      toast.success('Marked as done');
      fetchTasks();
    } catch (_) {
      toast.error('Update failed');
    }
  };

  const handleAddProject = async (name) => {
    try {
      await createProject({ name });
      toast.success('Project created');
      fetchProjects();
    } catch (_) {
      toast.error('Could not create project');
    }
  };

  const grouped = STATUSES.reduce((acc, s) => {
    acc[s] = tasks.filter((t) => t.status === s);
    return acc;
  }, {});

  return (
    <div className="app">
      <Toaster position="top-right" />

      <header className="app-header">
        <h1>🗂 Task Tracker</h1>
        <button className="btn btn-primary" onClick={() => { setEditTask(null); setShowModal(true); }}>
          + New Task
        </button>
      </header>

      <div className="app-body">
        <ProjectSidebar
          projects={projects}
          selected={filters.projectId}
          onSelect={(id) => { setFilters((f) => ({ ...f, projectId: id })); setPage(0); }}
          onAdd={handleAddProject}
        />

        <main className="main">
          <FilterBar
            filters={filters}
            sortBy={sortBy}
            sortDir={sortDir}
            onFilterChange={(key, val) => { setFilters((f) => ({ ...f, [key]: val })); setPage(0); }}
            onSortByChange={setSortBy}
            onSortDirChange={setSortDir}
          />

          {loading ? (
            <div className="loading">Loading…</div>
          ) : (
            <div className="board">
              {STATUSES.map((status) => (
                <div className="column" key={status}>
                  <h2 className={`col-header col-${status.toLowerCase()}`}>
                    {status} <span className="badge">{grouped[status].length}</span>
                  </h2>
                  {grouped[status].length === 0 && (
                    <p className="empty">No tasks</p>
                  )}
                  {grouped[status].map((task) => (
                    <TaskCard
                      key={task.id}
                      task={task}
                      onEdit={() => { setEditTask(task); setShowModal(true); }}
                      onDelete={() => handleDelete(task.id)}
                      onComplete={() => handleComplete(task)}
                    />
                  ))}
                </div>
              ))}
            </div>
          )}

          {totalPages > 1 && (
            <div className="pagination">
              <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}>← Prev</button>
              <span>Page {page + 1} / {totalPages}</span>
              <button disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>Next →</button>
            </div>
          )}
        </main>
      </div>

      {showModal && (
        <TaskModal
          task={editTask}
          projects={projects}
          onSave={handleSave}
          onClose={() => { setShowModal(false); setEditTask(null); }}
        />
      )}
    </div>
  );
}
