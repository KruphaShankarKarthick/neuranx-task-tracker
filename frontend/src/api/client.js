import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
});

// ─── Tasks ────────────────────────────────────────────────────────────────────

export const getTasks = (params) =>
  api.get('/tasks', { params }).then((r) => r.data);

export const getTask = (id) =>
  api.get(`/tasks/${id}`).then((r) => r.data);

export const createTask = (data) =>
  api.post('/tasks', data).then((r) => r.data);

export const updateTask = (id, data) =>
  api.put(`/tasks/${id}`, data).then((r) => r.data);

export const deleteTask = (id) =>
  api.delete(`/tasks/${id}`).then((r) => r.data);

// ─── Projects ─────────────────────────────────────────────────────────────────

export const getProjects = () =>
  api.get('/projects').then((r) => r.data);

export const createProject = (data) =>
  api.post('/projects', data).then((r) => r.data);

export const deleteProject = (id) =>
  api.delete(`/projects/${id}`).then((r) => r.data);

export default api;
