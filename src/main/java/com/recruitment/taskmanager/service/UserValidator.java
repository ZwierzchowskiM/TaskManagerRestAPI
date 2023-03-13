package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.dto.UserDto;
import com.recruitment.taskmanager.model.User;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

@Component
public class UserValidator implements Validator {

    String[] blackList = {"?", "|", "-", "="};

    public boolean supports(Class clazz) {
        return UserDto.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {

        UserDto user = (UserDto) obj;

        boolean blackListCheckFirstName    = Arrays.stream(blackList).anyMatch(user.getFirstName()::contains);
        boolean blackListCheckLastName = Arrays.stream(blackList).anyMatch(user.getLastName()::contains);

        if(blackListCheckFirstName) {
            e.reject("firstName", "first name contains forbidden characters");
        }

        if(blackListCheckLastName) {
            e.reject("lastName", "first name contains forbidden characters");
        }
    }
}