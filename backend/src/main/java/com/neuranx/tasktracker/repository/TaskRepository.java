package com.neuranx.tasktracker.repository;

import com.neuranx.tasktracker.entity.Task;
import com.neuranx.tasktracker.entity.enums.Priority;
import com.neuranx.tasktracker.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Filter by optional status and/or priority, sorted via Pageable.
     * SQL-level filtering — no in-memory filtering.
     */
    @Query("""
        SELECT t FROM Task t
        WHERE (:status IS NULL OR t.status = :status)
          AND (:priority IS NULL OR t.priority = :priority)
          AND (:projectId IS NULL OR t.project.id = :projectId)
        """)
    Page<Task> findAllFiltered(
            @Param("status") Status status,
            @Param("priority") Priority priority,
            @Param("projectId") Long projectId,
            Pageable pageable
    );
}
