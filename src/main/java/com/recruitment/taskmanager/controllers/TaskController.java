package com.recruitment.taskmanager.controllers;

import com.recruitment.taskmanager.dto.TaskDto;
import com.recruitment.taskmanager.enums.Status;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.model.Task;
import com.recruitment.taskmanager.repositories.TaskRepository;
import com.recruitment.taskmanager.service.TaskService;
import jakarta.validation.Valid;
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

    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<Task>> getTasks(@RequestParam(required = false) String title,
                                               @RequestParam(required = false) Status status,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) LocalDate dateBefore) {
        return ResponseEntity.ok(taskService.findTasks(title, status, userId, dateBefore));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        Task task = taskService.findTaskById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID :" + id + " Not Found"));
        return ResponseEntity.ok(task);
    }

    @PostMapping("/")
    ResponseEntity<Task> createTask(@Valid @RequestBody TaskDto taskDto) {
        Task newTask = taskService.createTask(taskDto);

        URI newTaskUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTask.getId())
                .toUri();
        return ResponseEntity.created(newTaskUri).body(newTask);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteTask(@PathVariable Long id) {

        if (taskRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Task with ID :" + id + " Not Found");
        }
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted");
    }

    @PatchMapping("/{id}/user-addition")
    ResponseEntity<Task> addUserToTask(@PathVariable Long id, @RequestParam Long userId) {

        Task editedTask = taskService.addUserToTask(userId, id);
        return ResponseEntity.ok(editedTask);
    }

    @PatchMapping("/{id}/user-removing")
    ResponseEntity<Task> removeUserFromTask(@PathVariable Long id, @RequestParam Long userId) {

        Task editedTask = taskService.removeUserFromTask(userId, id);
        return ResponseEntity.ok(editedTask);
    }

    @PatchMapping("/{id}/status")
    ResponseEntity<Task> changeTaskStatus(@PathVariable Long id, @RequestParam Status status) {

        Task editedTask = taskService.changeTaskStatus(status, id);
        return ResponseEntity.ok(editedTask);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Task>> getAllTasksByDueDateExpired() {
        return ResponseEntity.ok(taskService.findByDueDateExpired());
    }



}
