package com.example.clashcoding.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(unique = true)
    public String title;

    @NotBlank
    @Column
    public String description;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ExamplesIO> examples = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<TestCase> tc = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    public User user;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @CreatedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prepersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preupdate() {
        updatedAt = Instant.now();
    }
}
