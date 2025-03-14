package org.zerock.projects.controller.machines;

import org.zerock.projects.domain.machines.TaskType;

import javax.persistence.*;

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TaskType taskType;  // 각 공정별 task 종류
    private String description;     // 비고
    private int duration;           // Task 진행기간

    @ManyToOne      // 한 Process 안 많은 Task들
    @JoinColumn(name = "process_id")
    private Process process;

    private int progress;

    @lombok.Generated
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
        this.progress = progress;
    }
}
