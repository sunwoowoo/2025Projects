package org.zerock.projects.domain.machines;

import lombok.*;
import lombok.extern.log4j.Log4j2;
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

    private boolean completed;

    public void addTask(TaskType taskType) {
        if (!TaskType.belongsToProcess(taskType, this.type)) {
            throw new IllegalArgumentException(
                    "Cannot add task type " + taskType + " to process type " + this.type
            );
        }
        Task task = Task.builder()
                .taskType(taskType)
                .process(this)
                .progress(0)
                .completed(false)
                .build();
        tasks.add(task);
    }
}
