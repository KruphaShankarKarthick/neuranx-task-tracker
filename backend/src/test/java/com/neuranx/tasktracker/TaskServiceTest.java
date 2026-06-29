package com.neuranx.tasktracker;

import com.neuranx.tasktracker.dto.TaskDto;
import com.neuranx.tasktracker.entity.Task;
import com.neuranx.tasktracker.entity.enums.Priority;
import com.neuranx.tasktracker.entity.enums.Status;
import com.neuranx.tasktracker.exception.ResourceNotFoundException;
import com.neuranx.tasktracker.repository.ProjectRepository;
import com.neuranx.tasktracker.repository.TaskRepository;
import com.neuranx.tasktracker.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .title("Write unit tests")
                .description("Cover core service methods")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .dueDate(LocalDate.now().plusDays(3))
                .build();
        // reflectively set id since @Builder doesn't call setId
        sampleTask.setId(1L);
    }

    // ─── getTask ──────────────────────────────────────────────────────────────

    @Test
    void getTask_existingId_returnsResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskDto.Response resp = taskService.getTask(1L);

        assertThat(resp.id()).isEqualTo(1L);
        assertThat(resp.title()).isEqualTo("Write unit tests");
        assertThat(resp.status()).isEqualTo(Status.TODO);
    }

    @Test
    void getTask_missingId_throwsNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── createTask ───────────────────────────────────────────────────────────

    @Test
    void createTask_withNullStatus_defaultsTodo() {
        TaskDto.CreateRequest req = new TaskDto.CreateRequest(
                "New task", null, null, Priority.LOW, null, null);

        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        TaskDto.Response resp = taskService.createTask(req);

        assertThat(resp.status()).isEqualTo(Status.TODO);
        assertThat(resp.priority()).isEqualTo(Priority.LOW);
    }

    @Test
    void createTask_withNullPriority_defaultsMedium() {
        TaskDto.CreateRequest req = new TaskDto.CreateRequest(
                "Another task", null, Status.DOING, null, null, null);

        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(3L);
            return t;
        });

        TaskDto.Response resp = taskService.createTask(req);

        assertThat(resp.priority()).isEqualTo(Priority.MEDIUM);
    }

    // ─── deleteTask ───────────────────────────────────────────────────────────

    @Test
    void deleteTask_existingId_callsRepositoryDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        taskService.deleteTask(1L);

        verify(taskRepository).delete(sampleTask);
    }

    @Test
    void deleteTask_missingId_throwsNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).delete(any());
    }

    // ─── updateTask ───────────────────────────────────────────────────────────

    @Test
    void updateTask_existingId_updatesFields() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskDto.UpdateRequest req = new TaskDto.UpdateRequest(
                "Updated title", "New desc", Status.DONE, Priority.LOW,
                LocalDate.now().plusDays(7), null);

        TaskDto.Response resp = taskService.updateTask(1L, req);

        assertThat(resp.title()).isEqualTo("Updated title");
        assertThat(resp.status()).isEqualTo(Status.DONE);
        assertThat(resp.priority()).isEqualTo(Priority.LOW);
    }
}
