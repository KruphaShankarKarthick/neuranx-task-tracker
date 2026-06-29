import React from 'react';

export default function FilterBar({ filters, sortBy, sortDir, onFilterChange, onSortByChange, onSortDirChange }) {
  return (
    <div className="filter-bar">
      <select
        value={filters.status}
        onChange={(e) => onFilterChange('status', e.target.value)}
      >
        <option value="">All Statuses</option>
        {['TODO', 'DOING', 'DONE'].map((s) => <option key={s}>{s}</option>)}
      </select>

      <select
        value={filters.priority}
        onChange={(e) => onFilterChange('priority', e.target.value)}
      >
        <option value="">All Priorities</option>
        {['LOW', 'MEDIUM', 'HIGH'].map((p) => <option key={p}>{p}</option>)}
      </select>

      <select value={sortBy} onChange={(e) => onSortByChange(e.target.value)}>
        <option value="dueDate">Sort: Due Date</option>
        <option value="priority">Sort: Priority</option>
        <option value="createdAt">Sort: Created</option>
        <option value="title">Sort: Title</option>
      </select>

      <button
        className="btn sort-dir"
        onClick={() => onSortDirChange(sortDir === 'asc' ? 'desc' : 'asc')}
        title="Toggle sort direction"
      >
        {sortDir === 'asc' ? '↑ Asc' : '↓ Desc'}
      </button>
    </div>
  );
}
