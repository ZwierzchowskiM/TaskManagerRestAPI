package com.recruitment.taskmanager.model;

import com.recruitment.taskmanager.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @NotNull
    @Size(min = 2, max = 100)
    private String title;
    @NotNull
    @Size(min = 2, max = 300)
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToMany
    private List<User> users = new ArrayList<>();
    @Temporal(TemporalType.DATE)
    private LocalDate dueDate;

}
