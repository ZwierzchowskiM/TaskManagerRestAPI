package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.dto.UserDto;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.model.User;
import com.recruitment.taskmanager.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
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
class UserServiceTest {

    User user1;
    User user2;
    User user3;
    UserDto userDto;
    List<User> allUsers;
    List<User> filteredUsers;

    @Autowired
    UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "John", "Doe", "john@gmail.com", 18,null);
        user2 = new User(2L, "Jane", "Smith", "jane@gmail.com", 23,null);
        user3 = new User(3L, "John", "Smith", "smith@gmail.com", 40,null);
        userDto = new UserDto("Joe", "Bloggs", "joe@gmail.com", 24);
    }

    @Test
    void findAllUsers_whenSearchNull_then_returnAllUsers() {

        //given
        allUsers = Arrays.asList(user1, user2);
        UserSpecification specification = new UserSpecification();
        when(userRepository.findAll(specification)).thenReturn(allUsers);

        //when
        List<User> users = userService.findAllUsers(specification);

        //then
        assertThat(users).hasSize(2).extracting(User::getFirstName).contains("John", "Jane");

    }

    @Test
    void findUserById_then_returnUser() {

        //given
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        //when
        Optional<User> fromDb = userService.findUserById(1L);

        //then
        assertThat(fromDb.get().getId()).isEqualTo(1L);
        assertThat(fromDb.get().getEmail()).isEqualTo("john@gmail.com");
    }

    @Test
    void saveUser_then_returnSavedUser() {

        //given
        when(userRepository.save(user2)).thenReturn(user2);

        //when
        User savedUser =  userService.saveUser(userDto);

        //then
        assertThat(savedUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void deleteUser_then_deleteUser() {

        //given
        doNothing().when(userRepository).deleteById(user1.getId());

        //when
        userService.deleteUser(1L);

        //then
        verify(userRepository).deleteById(1L);
    }

    @Test
    void updatedUser_whenValidUserId_shouldUpdateUser() {
        //given

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);

        //when
        userDto.setEmail("newmail@gmail.com");
        User updatedUser = userService.updateUser(1L, userDto);

        //then
        assertThat(updatedUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void updatedUser_whenInValidUserId_shouldThrowUserExistedInTaskException() {

        //given
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> userService.updateUser(1L, userDto))
                .isExactlyInstanceOf(ResourceNotFoundException.class);
    }
}