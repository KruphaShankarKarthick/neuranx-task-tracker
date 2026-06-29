package com.neuranx.tasktracker.service;

import com.neuranx.tasktracker.dto.PagedResponse;
import com.neuranx.tasktracker.dto.TaskDto;
import com.neuranx.tasktracker.entity.Project;
import com.neuranx.tasktracker.entity.Task;
import com.neuranx.tasktracker.entity.enums.Priority;
import com.neuranx.tasktracker.entity.enums.Status;
import com.neuranx.tasktracker.exception.ResourceNotFoundException;
import com.neuranx.tasktracker.repository.ProjectRepository;
import com.neuranx.tasktracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public PagedResponse<TaskDto.Response> listTasks(
            Status status, Priority priority, Long projectId,
            int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> result = taskRepository.findAllFiltered(status, priority, projectId, pageable);

        return new PagedResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    @Transactional(readOnly = true)
    public TaskDto.Response getTask(Long id) {
        return toResponse(findTaskById(id));
    }

    public TaskDto.Response createTask(TaskDto.CreateRequest req) {
        Task task = Task.builder()
                .title(req.title())
                .description(req.description())
                .status(req.status() != null ? req.status() : Status.TODO)
                .priority(req.priority() != null ? req.priority() : Priority.MEDIUM)
                .dueDate(req.dueDate())
                .project(resolveProject(req.projectId()))
                .build();
        return toResponse(taskRepository.save(task));
    }

    public TaskDto.Response updateTask(Long id, TaskDto.UpdateRequest req) {
        Task task = findTaskById(id);
        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setStatus(req.status() != null ? req.status() : task.getStatus());
        task.setPriority(req.priority() != null ? req.priority() : task.getPriority());
        task.setDueDate(req.dueDate());
        task.setProject(resolveProject(req.projectId()));
        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    private Project resolveProject(Long projectId) {
        if (projectId == null) return null;
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    private TaskDto.Response toResponse(Task t) {
        return new TaskDto.Response(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getDueDate(),
                t.getProject() != null ? t.getProject().getId() : null,
                t.getProject() != null ? t.getProject().getName() : null,
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
