package org.zerock.projects.domain.machines;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne      // 한 Process 안 많은 Task들
    @JoinColumn(name = "process_id")
    private Process process;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;  // 각 공정별 task 종류

    private boolean completed;
    private int progress;

    private void validateTaskType() {
        if (!TaskType.belongsToProcess(taskType, process.getType())) {
            throw new IllegalStateException(
                    "Task type " + taskType + " is not valid for process type " + process.getType()
            );
        }
    }
}
