package com.recruitment.taskmanager.model;

import com.recruitment.taskmanager.enums.Status;
import jakarta.persistence.*;
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
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToMany
    private List<User> users = new ArrayList<>();
    @Temporal(TemporalType.DATE)
    private LocalDate dueDate;

}
