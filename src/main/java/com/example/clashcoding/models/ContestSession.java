package com.example.clashcoding.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "contestsession")
public class ContestSession {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contestId", nullable = false)
    private Contest contestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID", nullable = false)
    private User userID;

    @Column(name = "sessionStart")
    private Instant sessionStart;

    @Column(name = "sessionEnd")
    private Instant sessionEnd;

    @PrePersist
    private void prepersist() {
        Instant now = Instant.now();
        sessionStart = now;
    }
}
