package com.recruitment.taskmanager.controllers;

import com.recruitment.taskmanager.dto.TaskDto;
import com.recruitment.taskmanager.enums.Status;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.model.Task;
import com.recruitment.taskmanager.repositories.TaskRepository;
import com.recruitment.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.path}/tasks")
public class TaskController {

    TaskService taskService;
    TaskRepository taskRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<Task>> getTasks(@RequestParam(required = false) String title,
                                               @RequestParam(required = false) Status status,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) LocalDate dateBefore) {

        LOGGER.info("Getting all tasks");

        return ResponseEntity.ok(taskService.findTasks(title, status, userId, dateBefore));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {

        LOGGER.info("Getting info about task {}", id);

        Task task = taskService.findTaskById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID :" + id + " Not Found"));
        return ResponseEntity.ok(task);
    }

    @PostMapping("/")
    ResponseEntity<Task> createTask(@Valid @RequestBody TaskDto taskDto) {

        LOGGER.info("Creating new task");

        Task newTask = taskService.createTask(taskDto);

        URI newTaskUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTask.getId())
                .toUri();
        return ResponseEntity.created(newTaskUri).body(newTask);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteTask(@PathVariable Long id) {

        LOGGER.info("Deleting task {}", id);

        if (taskRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Task with ID :" + id + " Not Found");
        }
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted");
    }

    @PatchMapping("/{id}/user-addition")
    ResponseEntity<Task> addUserToTask(@PathVariable Long id, @RequestParam Long userId) {

        LOGGER.info("adding user {} to task {}", userId,id);

        Task editedTask = taskService.addUserToTask(userId, id);
        return ResponseEntity.ok(editedTask);
    }

    @PatchMapping("/{id}/user-removing")
    ResponseEntity<Task> removeUserFromTask(@PathVariable Long id, @RequestParam Long userId) {

        LOGGER.info("removing user {} from task {}", userId,id);


        Task editedTask = taskService.removeUserFromTask(userId, id);
        return ResponseEntity.ok(editedTask);
    }

    @PatchMapping("/{id}/status")
    ResponseEntity<Task> changeTaskStatus(@PathVariable Long id, @RequestParam Status status) {

        LOGGER.info("changing task {} status",id);

        Task editedTask = taskService.changeTaskStatus(status, id);
        return ResponseEntity.ok(editedTask);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Task>> getAllTasksByDueDateExpired() {

        LOGGER.info("Getting expired tasks");

        return ResponseEntity.ok(taskService.findByDueDateExpired());
    }



}
