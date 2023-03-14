package com.recruitment.taskmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TaskDto {

    @NotNull
    @Size(min = 2, max = 100)
    private String title;
    @NotNull
    @Size(min = 2, max = 300)
    private String description;
    private Long[] userIds;
    private LocalDate dueDate;

}
