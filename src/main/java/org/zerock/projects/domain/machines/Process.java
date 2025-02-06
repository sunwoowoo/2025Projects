package org.zerock.projects.domain.machines;

import lombok.*;
import org.zerock.projects.domain.ProductionOrder;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Process {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProcessType processType;  // 프레싱(PRESSING), 차체용접(WELDING), 도장(PAINTING), 조립(ASSEMBLYING)

    private String description;     // 비고

    @OneToMany(mappedBy = "process")    // Process 안에 여러개의 task (task1, task2, task3 task4)
    private List<Task> tasks;

    private int progress;

    @Generated
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
        this.progress = progress;
    }

    public void updateProgress() {
        if (tasks == null || tasks.isEmpty()) {
            this.progress = 0;
        } else {
            int totalProgress = tasks.stream().mapToInt(Task::getProgress).sum();
            this.progress = totalProgress / tasks.size();
        }
    }

    public ProductionOrder getProductionOrder() {
        return null;
    }
}
