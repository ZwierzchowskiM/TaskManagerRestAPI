package com.recruitment.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TaskDto {

    private String title;
    private String description;
    private Long[] userIds;
    private LocalDate dueDate;

}
