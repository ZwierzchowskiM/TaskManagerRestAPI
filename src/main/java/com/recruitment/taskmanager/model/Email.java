package com.recruitment.taskmanager.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Email {

    // Class data members
    private String recipient;
    private String msgBody;
    private String subject;
}