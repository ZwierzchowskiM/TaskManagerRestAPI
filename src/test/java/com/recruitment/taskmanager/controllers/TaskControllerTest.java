package com.recruitment.taskmanager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recruitment.taskmanager.dto.TaskDto;
import com.recruitment.taskmanager.enums.Status;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.model.Task;
import com.recruitment.taskmanager.model.User;
import com.recruitment.taskmanager.repositories.TaskRepository;
import com.recruitment.taskmanager.service.TaskService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class TaskControllerTest {

    Task task1;
    Task task2;
    Task task3;
    TaskDto taskDto;
    List<Task> filteredTasks;
    List<Task> expiredTasks;
    User user1;
    User user2;
    List <User> emptyList = new ArrayList<>();
    List <User> users = new ArrayList<>();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService taskService;
    @MockBean
    private TaskRepository taskRepository;


    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "John", "Doe", "john@gmail.com",18, null);
        user2 = new User(2L, "Jane", "Smith", "smith@gmail.com",23, null);
        users.add(user1);
        users.add(user2);
        task1 = new Task(1L,"firstTask", "This is first tested task", Status.OPENED,users, LocalDate.of(2023, 1, 8));
        task2 = new Task(2L,"secondTask", "This is second tested task", Status.OPENED,emptyList, LocalDate.of(2021, 4, 8));
        task3 = new Task(3L,"thirdTask", "This is third tested task", Status.INPROGRESS,List.of(user2), LocalDate.of(2022, 1, 8));
        taskDto = new TaskDto("firstTask", "This is first tested task", new Long[]{2L},LocalDate.of(2020, 1, 8));

        filteredTasks = Arrays.asList(task1, task3);
        expiredTasks = Arrays.asList(task1, task2);
    }

    @Test
    void getTasks_whenSearchNull_should_getAllTasks() throws Exception {

        List<Task> allTasks = Arrays.asList(task1,task2,task3);
        given(taskService.findTasks(null,null, null, null)).willReturn(allTasks);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/tasks/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title", is("firstTask"))).
                andExpect(jsonPath("$[1].title", is("secondTask")));

    }

    @Test
    void getTasks_whenSearch_should_getTasks() throws Exception {

        given(taskService.findTasks(null,Status.INPROGRESS, null, null)).willReturn(List.of(task3));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/tasks/")
                        .param( "status", String.valueOf(Status.INPROGRESS))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("thirdTask")));
    }

    @Test
    void getTaskById_should_returnTask() throws Exception {
        when(taskService.findTaskById(task1.getId())).thenReturn(Optional.of(task1));

        mockMvc.perform(get("/api/v1/tasks/" + task1.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("firstTask")));
    }

    @Test
    void getTask_should_throwResourceNotFoundException() throws Exception {
        when(taskService.findTaskById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tasks/" + 99L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void createTask_shouldReturnTask() throws Exception {

        given(taskService.createTask(Mockito.any(TaskDto.class))).willReturn(task1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/tasks/")
                        .content(asJsonString(taskDto))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("firstTask")));
    }

    @Test
    void deleteTask_validId_statusIsOk() throws Exception {

        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));
        doNothing().when(taskService).deleteTask(task1.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/tasks/" + task1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTask_InvalidId_ResourceNotFoundException() throws Exception {

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        doNothing().when(taskService).deleteTask(task1.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/tasks/" + 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void addUserToTask_should() throws Exception {

        given(taskService.addUserToTask(user2.getId(),task3.getId())).willReturn(task3);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/tasks/"+task3.getId() +"/user-addition")
                        .param( "userId", user2.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[0].id", is(2)));
    }

    @Test
    void removeUserFromTask() throws Exception {
        given(taskService.removeUserFromTask(user2.getId(),task2.getId())).willReturn(task2);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/tasks/"+task2.getId() +"/user-removing")
                        .param( "userId", user2.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", is(emptyList)));
    }

    @Test
    void changeTaskStatus() throws Exception {
        given(taskService.changeTaskStatus(Status.OPENED,task2.getId())).willReturn(task2);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/tasks/"+task2.getId() +"/status")
                        .param( "status", Status.OPENED.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Status.OPENED.toString())));
    }



    @Test
    void getAllTasksByDueDateExpired() throws Exception {
        given(taskService.findByDueDateExpired()).willReturn(List.of(task2,task3));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/tasks/expired")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("secondTask")))
                .andExpect(jsonPath("$[1].title", is("thirdTask")));
    }

    private static String asJsonString(final Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            return  objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}