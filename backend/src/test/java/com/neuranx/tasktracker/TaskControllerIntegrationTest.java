package com.neuranx.tasktracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuranx.tasktracker.dto.TaskDto;
import com.neuranx.tasktracker.entity.enums.Priority;
import com.neuranx.tasktracker.entity.enums.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_validRequest_returns201() throws Exception {
        TaskDto.CreateRequest req = new TaskDto.CreateRequest(
                "Integration test task", "desc", Status.TODO,
                Priority.HIGH, LocalDate.now().plusDays(5), null);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration test task"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void createTask_blankTitle_returns422() throws Exception {
        TaskDto.CreateRequest req = new TaskDto.CreateRequest(
                "", "desc", null, null, null, null);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message.title").exists());
    }

    @Test
    void getTask_nonExistentId_returns404() throws Exception {
        mockMvc.perform(get("/api/tasks/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createAndDelete_task_returns204() throws Exception {
        // Create
        TaskDto.CreateRequest req = new TaskDto.CreateRequest(
                "Task to delete", null, Status.TODO, Priority.LOW, null, null);

        String body = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(body).get("id").asLong();

        // Delete
        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isNoContent());

        // Confirm gone
        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void listTasks_returnsPagedResult() throws Exception {
        // Create a task first
        TaskDto.CreateRequest req = new TaskDto.CreateRequest(
                "Listed task", null, Status.DOING, Priority.MEDIUM, null, null);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/tasks")
                        .param("status", "DOING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0));
    }
}
