package com.recruitment.taskmanager.controllers;

import com.recruitment.taskmanager.dto.UserDto;
import com.recruitment.taskmanager.exceptions.ResourceNotFoundException;
import com.recruitment.taskmanager.model.User;
import com.recruitment.taskmanager.repositories.UserRepository;
import com.recruitment.taskmanager.service.SearchCriteria;
import com.recruitment.taskmanager.service.UserService;
import com.recruitment.taskmanager.service.UserSpecification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("${api.path}/users")
public class UserController {

    UserService userService;
    UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String search) {

        UserSpecification specification = new UserSpecification();
        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+)(:|<|>|>=|<=)(\\w+),", Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                specification.add(new SearchCriteria(matcher.group(1),
                        matcher.group(2), matcher.group(3)));
            }
        }
        List<User> users = userService.findAllUsers(specification);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {

        User user = userService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID :" + id + " Not Found"));

        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/")
    ResponseEntity<User> createUser(@RequestBody UserDto createdUser) {
        User newUser = userService.saveUser(createdUser);

        URI newTaskUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.getId())
                .toUri();
        return ResponseEntity.created(newTaskUri).body(newUser);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteUser(@PathVariable Long id) {

        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("User with ID :" + id + " Not Found");
        }
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

    @PutMapping("/{id}")
    ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDto user) {

        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok().body(updatedUser);
    }


}
