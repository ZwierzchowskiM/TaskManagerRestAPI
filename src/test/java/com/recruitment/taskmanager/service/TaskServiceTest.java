package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.dto.TaskDto;
import com.recruitment.taskmanager.enums.Status;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.exceptions.UserExistedInTaskException;
import com.recruitment.taskmanager.model.Task;
import com.recruitment.taskmanager.model.User;
import com.recruitment.taskmanager.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class TaskServiceTest {

    Task task1;
    Task task2;
    Task task3;
    TaskDto taskDto;
    List<Task> allTasks;
    List<Task> filteredTasks;
    List<Task> expiredTasks;

    User user1;
    User user2;
    List <User> emptyList = new ArrayList<>();
    List <User> users = new ArrayList<>();



    @Autowired
    TaskService taskService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserIdMapper userIdMapper;
    @MockBean
    private TaskRepository taskRepository;

    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "John", "Doe", "john@gmail.com",18, null);
        user2 = new User(2L, "Jane", "Smith", "jane@gmail.com", 23, null);
        users.add(user1);
        users.add(user2);
        task1 = new Task(1L,"firstTask", "This is first tested task", Status.OPENED,users, LocalDate.of(2023, 1, 8));
        task2 = new Task(2L,"secondTask", "This is second tested task", Status.OPENED,emptyList, LocalDate.of(2021, 4, 8));
        task3 = new Task(3L,"thirdTask", "This is third tested task", Status.OPENED,List.of(user2), LocalDate.of(2022, 1, 8));
        taskDto = new TaskDto("firstTask", "This is first tested task", new Long[]{2L},LocalDate.of(2020, 1, 8));

        filteredTasks = Arrays.asList(task1, task3);
        expiredTasks = Arrays.asList(task1, task2);

    }

    @Test
    void findAll_whenSearchNull_then_returnAllTasks() {

        //given
        allTasks = Arrays.asList(task1, task2,task3);
        when(taskRepository.findTasks(null,null,null,null)).thenReturn(allTasks);

        //when
        List<Task> tasks = taskService.findTasks(null,null,null,null);

        //then
        assertThat(tasks).hasSize(3).extracting(Task::getTitle).contains("firstTask", "secondTask","thirdTask");

    }

        @Test
    void findAll_whenSearch_then_returnAllTasks() {
        //given
        filteredTasks = Arrays.asList(task1, task3);
        when(taskRepository.findTasks(null,null,2L,null)).thenReturn(filteredTasks);

        //when
        List<Task> tasks= taskService.findTasks(null,null,2L,null);

        //then
        assertThat(tasks).hasSize(2).extracting(Task::getId).contains(1L, 3L);
    }


    @Test
    void findById_then_returnTask() {
        //given
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));

        //when
        Optional<Task> task = taskService.findTaskById(1L);

        //then
        assertThat(task.get().getId()).isEqualTo(1L);
        assertThat(task.get().getTitle()).isEqualTo("firstTask");
    }

    @Test
    void createTask_then_returnCreatedTask() {

        //given
        when(userIdMapper.mapToUserList(List.of(2L))).thenReturn(List.of(user2));
        when(taskRepository.save(task3)).thenReturn(task3);

        //when
        Task savedTask =  taskService.createTask(taskDto);

        //then
        assertThat(savedTask.getTitle()).isEqualTo(taskDto.getTitle());
        assertThat(savedTask.getUsers().get(0).getId()).isEqualTo(taskDto.getUserIds()[0]);
    }

    @Test
    void deleteById_then_deleteTask() {
        //given
        doNothing().when(taskRepository).deleteById(task1.getId());

        //when
        taskService.deleteTask(1L);

        //then
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void addUserToTask_when_ValidTaskId_then_addUserToTask() {
        //given
        when(userIdMapper.mapToUser(user2.getId())).thenReturn(user2);
        when(taskRepository.findById(task2.getId())).thenReturn(Optional.of(task2));

        //when
        Task task = taskService.addUserToTask(user2.getId(),task2.getId());

        //then
        assertThat(task.getUsers()).hasSize(1).extracting(User::getId).contains(2L);

    }

    @Test
    void addUserToTask_when_InValidTaskId_thenThrowResourceNotFoundException() {

        when(taskRepository.findById(task2.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.addUserToTask(user2.getId(),task2.getId()))
                .isExactlyInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void addUserToTask_when_UserIdExistedInTask_thenThrowUserExistedInTaskException() {

        when(userIdMapper.mapToUser(user1.getId())).thenReturn(user1);
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));

        assertThatThrownBy(() -> taskService.addUserToTask(user1.getId(),task1.getId()))
                .isExactlyInstanceOf(UserExistedInTaskException.class);

    }

    @Test
    void removeUserFromTask_whenValidTaskId_thenRemoveUserFromTask() {
        //given
        when(userIdMapper.mapToUser(user1.getId())).thenReturn(user1);
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));

        //when
        Task task = taskService.removeUserFromTask(user1.getId(),task1.getId());

        //then
        assertThat(task.getUsers()).hasSize(1).contains(user2);
    }

    @Test
    void removeUserFromTask_whenInValidTaskId_thenThrowResourceNotFoundException() {
        //given
        when(userIdMapper.mapToUser(user1.getId())).thenReturn(user1);
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.removeUserFromTask(user1.getId(),task1.getId()))
                .isExactlyInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void changeTaskStatus_whenValidTaskId_thenReturnChangedTask() {
        //given
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));

        //when
        Task task = taskService.changeTaskStatus(Status.COMPLETED,task1.getId());

        //then
        assertThat(task.getStatus()).isEqualTo(Status.COMPLETED);

    }

    @Test
    void changeTaskStatus_whenInValidTaskId() {
        //given
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> taskService.changeTaskStatus(Status.OPENED, task1.getId()))
                .isExactlyInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void listByDueDateExpired_then_returnListOfTasks() {
        //given
        expiredTasks = Arrays.asList(task2, task3);
        when(taskRepository.findAllWithDueDateExpired(Mockito.any(LocalDate.class))).thenReturn(expiredTasks);

        //when
        List<Task> tasks= taskService.findByDueDateExpired();

        //then
        assertThat(tasks).hasSize(2).extracting(Task::getId).contains(2L, 3L);
    }
}