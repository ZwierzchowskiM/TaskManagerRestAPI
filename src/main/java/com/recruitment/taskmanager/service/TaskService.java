package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.dto.TaskDto;
import com.recruitment.taskmanager.enums.Status;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.exceptions.UserExistedInTaskException;
import com.recruitment.taskmanager.model.Task;
import com.recruitment.taskmanager.model.User;
import com.recruitment.taskmanager.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    TaskRepository taskRepository;
    UserIdMapper userIdMapper;

    public TaskService(TaskRepository taskRepository, UserIdMapper userIdMapper) {
        this.taskRepository = taskRepository;
        this.userIdMapper = userIdMapper;
    }

    @Transactional
    public List<Task> findTasks(String title, Status status, Long userId, LocalDate dateBefore) {
        return taskRepository.findTasks(title,status,userId, dateBefore);
    }

    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public Task createTask(TaskDto taskDto) {
        Task newTask = new Task();
        newTask.setTitle(taskDto.getTitle());
        newTask.setDescription(taskDto.getDescription());
        newTask.setStatus(Status.OPENED);
        newTask.setUsers(userIdMapper.mapToUserList(Arrays.asList(taskDto.getUserIds())));
        newTask.setDueDate(taskDto.getDueDate());
        taskRepository.save(newTask);

        return newTask;
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional
    public Task addUserToTask(Long userId, Long taskId) {
        User user = userIdMapper.mapToUser(userId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID :" + taskId + " Not Found"));

        if (task.getUsers() != null && task.getUsers().contains(user)) {
            throw new UserExistedInTaskException("User with ID :" + userId + "already exist in task");
        }
        task.getUsers().add(user);

        return task;
    }

    @Transactional
    public Task removeUserFromTask(Long userId, Long taskId) {
        User user = userIdMapper.mapToUser(userId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID :" + taskId + " Not Found"));
        task.getUsers().remove(user);

        return task;
    }

    @Transactional
    public Task changeTaskStatus(Status status, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID :" + taskId + " Not Found"));
        task.setStatus(status);

        return task;
    }

    public List<Task> findByDueDateExpired() {
        LocalDate localDate = LocalDate.now();
        List<Task> tasks = new ArrayList<>(taskRepository.findAllWithDueDateExpired(localDate));
        return tasks;
    }

}

