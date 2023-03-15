package com.recruitment.taskmanager.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Email {

    String to;
    String from;
    String subject;
    String text;
    String template;
    Map<String, Object> properties;
}