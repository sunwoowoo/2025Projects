package org.zerock.projects.repository.machines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // 공정 ID로 프레스 Task 찾기
    List<Task> findByProcessId(Long processId);
    // 수정 필요
    List<Task> findByProcessProcessType(ProcessType processType);
}
