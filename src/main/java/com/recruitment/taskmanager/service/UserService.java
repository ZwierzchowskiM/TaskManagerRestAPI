package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.dto.UserDto;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.model.User;
import com.recruitment.taskmanager.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers(UserSpecification specification) {
        return (List<User>) userRepository.findAll(specification);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User saveUser(UserDto newUser) {
        User savedUser = new User();
        savedUser.setFirstName(newUser.getFirstName());
        savedUser.setLastName(newUser.getLastName());
        savedUser.setEmail(newUser.getEmail());
        savedUser.setAge(newUser.getAge());
        userRepository.save(savedUser);
        return savedUser;
    }

    public User updateUser(Long id, UserDto userDto) {
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID :" + id + " Not Found"));
        updatedUser.setFirstName(userDto.getFirstName());
        updatedUser.setLastName(userDto.getLastName());
        updatedUser.setEmail(userDto.getEmail());
        updatedUser.setAge(userDto.getAge());
        return userRepository.save(updatedUser);
    }

}
