package com.recruitment.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private int age;

}