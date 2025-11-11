package com.example.escola.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String action;

    @Column(columnDefinition = "text")
    private String details;

    private Instant createdAt = Instant.now();

    // Convenience constructor used by the Aspect
    public ActivityLog(String username, String action, String details) {
        this.username = username;
        this.action = action;
        this.details = details;
        this.createdAt = Instant.now();
    }
}
