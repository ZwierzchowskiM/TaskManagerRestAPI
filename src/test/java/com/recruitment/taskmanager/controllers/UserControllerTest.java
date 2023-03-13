package com.recruitment.taskmanager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recruitment.taskmanager.dto.UserDto;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.model.User;
import com.recruitment.taskmanager.repositories.UserRepository;
import com.recruitment.taskmanager.service.UserService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerTest {

    User user1;
    User user2;
    User user3;
    UserDto userDto;
    List<User> allUsers = new ArrayList<>();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "John", "Doe", "john@gmail.com", 18,null);
        user2 = new User(2L, "Jane", "Smith", "jane@gmail.com", 23, null);
        user3 = new User(3L, "John", "Smith", "smith@gmail.com", 40,null);
        userDto = new UserDto("Joe", "Bloggs", "joe@gmail.com", 24);
        allUsers.add(user1);
        allUsers.add(user2);
        allUsers.add(user3);
    }

    @Test
    void getUsers_whenSearchIsNull_should_getAllUsers() throws Exception {

        when(userService.findAllUsers(Mockito.any())).thenReturn(allUsers);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].firstName", is("John"))).
                andExpect(jsonPath("$[1].firstName", is("Jane")));
    }

    @Test
    void getUsers_whenSearch_should_getAllUsers() throws Exception {

        String search = "firstName:John";
        when(userService.findAllUsers(Mockito.any())).thenReturn(List.of(user1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/")
                        .param( "search", search)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    void getUser_whenValidId_should_getUser() throws Exception {
        when(userService.findUserById(user1.getId())).thenReturn(Optional.of(user1));

        mockMvc.perform(get("/api/v1/users/" + user1.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is("John")));
    }

    @Test
    void getUser_whenInValidId_should_throwResourceNotFoundException() throws Exception {
        when(userService.findUserById((99L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/" + user1.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }


    @Test
    void createUser_shouldReturnUser() throws Exception {

        given(userService.saveUser(Mockito.any(UserDto.class))).willReturn(user1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/")
                        .content(asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    void deleteUser_whenValidId_shouldReturnIsOk() throws Exception {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        doNothing().when(userService).deleteUser(user1.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/" + user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_whenInValidId_should_throwResourceNotFoundException() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        doNothing().when(userService).deleteUser(user1.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/" + 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void updateUser() throws Exception {

        when(userService.updateUser(user3.getId(), userDto)).thenReturn(user3);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/" + user3.getId())
                        .content(asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200));
    }

    private static String asJsonString(final Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}