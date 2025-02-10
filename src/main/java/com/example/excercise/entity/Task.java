package com.example.excercise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NonNull
    private String externalId;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @ManyToOne
    @NonNull
    private Project project;
    private LocalDateTime deletedAt;

}
