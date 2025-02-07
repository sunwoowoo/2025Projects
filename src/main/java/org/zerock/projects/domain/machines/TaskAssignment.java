package org.zerock.projects.domain.machines;

import lombok.*;
import org.zerock.projects.domain.Worker;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TaskAssignment {   // Task에 필요한 자원 투입 (수정 필요)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;     // 공장 설비

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;          // 직원

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // e.g., PENDING, IN_PROGRESS, COMPLETED
}
