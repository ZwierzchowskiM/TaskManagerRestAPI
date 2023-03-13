package com.recruitment.taskmanager.exceptions;


public class UserExistedInTaskException extends RuntimeException {
    public UserExistedInTaskException(String s) {
        super(s);
    }
}