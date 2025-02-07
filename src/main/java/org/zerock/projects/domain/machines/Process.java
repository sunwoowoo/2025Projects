package org.zerock.projects.domain.machines;

import lombok.*;
<<<<<<< HEAD
import lombok.extern.log4j.Log4j2;
=======
>>>>>>> 1240204c22387519ba467394aa2e224ce2b1a583
import org.zerock.projects.domain.ProductionOrder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Process {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "production_order_id")
    private ProductionOrder productionOrder;

    @OneToMany(mappedBy = "process")    // Process 안에 여러개의 task (task1, task2, task3 task4)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProcessType type;  // 프레싱(PRESSING), 차체용접(WELDING), 도장(PAINTING), 조립(ASSEMBLYING)

<<<<<<< HEAD
    private boolean completed;

    public void addTask(TaskType taskType) {
        if (!TaskType.belongsToProcess(taskType, this.type)) {
            throw new IllegalArgumentException(
                    "Cannot add task type " + taskType + " to process type " + this.type
            );
=======
    @Generated
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
>>>>>>> 1240204c22387519ba467394aa2e224ce2b1a583
        }
        Task task = Task.builder()
                .taskType(taskType)
                .process(this)
                .progress(0)
                .completed(false)
                .build();
        tasks.add(task);
    }
<<<<<<< HEAD
=======

    public void updateProgress() {
        if (tasks == null || tasks.isEmpty()) {
            this.progress = 0;
        } else {
            int totalProgress = tasks.stream().mapToInt(Task::getProgress).sum();
            this.progress = totalProgress / tasks.size();
        }
    }

    public ProductionOrder getProductionOrder() {
        ProductionOrder productionOrder = new ProductionOrder();
        return productionOrder;
    }
>>>>>>> 1240204c22387519ba467394aa2e224ce2b1a583
}
