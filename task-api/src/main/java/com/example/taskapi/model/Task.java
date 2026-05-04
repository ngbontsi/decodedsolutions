package com.example.taskapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false, name = "user_id")
    private String userId;

    public Task(String title, String description, boolean completed, String userId) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.userId = userId;
    }
}
