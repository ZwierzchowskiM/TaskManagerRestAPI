package com.recruitment.taskmanager.repositories;

import com.recruitment.taskmanager.enums.Status;
import com.recruitment.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> , JpaSpecificationExecutor<Task> {

    @Query("SELECT c FROM Task c JOIN c.users u WHERE (:title is null or c.title = :title) and (:status is null"
            + " or c.status = :status) and  (:userId is null or u.id = :userId)  and "
            + "(:dateBefore is null or c.dueDate < :dateBefore)")
    List<Task> findTasks(String title, Status status, Long userId,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateBefore);


    @Query("select a from Task a where a.dueDate < :dueDate")
    List<Task> findAllWithDueDateExpired(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate);
}
