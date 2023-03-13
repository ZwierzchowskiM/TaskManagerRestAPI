package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.model.User;
import com.recruitment.taskmanager.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserIdMapper {

    UserRepository userRepository;

    public UserIdMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> mapToUserList(List<Long> userIds) {
        return userIds.stream()
                .map(this::mapToUser)
                .toList();
    }

    public User mapToUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID :" + id + " Not Found"));
    }


}
