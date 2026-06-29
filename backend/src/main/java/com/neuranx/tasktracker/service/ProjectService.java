package com.neuranx.tasktracker.service;

import com.neuranx.tasktracker.dto.ProjectDto;
import com.neuranx.tasktracker.entity.Project;
import com.neuranx.tasktracker.exception.ResourceNotFoundException;
import com.neuranx.tasktracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<ProjectDto.Response> listProjects() {
        return projectRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto.Response getProject(Long id) {
        return toResponse(findById(id));
    }

    public ProjectDto.Response createProject(ProjectDto.CreateRequest req) {
        Project p = Project.builder()
                .name(req.name())
                .description(req.description())
                .build();
        return toResponse(projectRepository.save(p));
    }

    public ProjectDto.Response updateProject(Long id, ProjectDto.CreateRequest req) {
        Project p = findById(id);
        p.setName(req.name());
        p.setDescription(req.description());
        return toResponse(projectRepository.save(p));
    }

    public void deleteProject(Long id) {
        Project p = findById(id);
        projectRepository.delete(p);
    }

    private Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private ProjectDto.Response toResponse(Project p) {
        return new ProjectDto.Response(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getTasks().size(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
