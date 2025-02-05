package org.zerock.projects.repository.machines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.domain.machines.PressTask;

import java.util.List;

public interface TaskRepository extends JpaRepository<PressTask, Long> {
    // 공정 ID로 프레스 Task 찾기
    List<PressTask> findByProcessId(Long processId);
    // 수정 필요
    List<PressTask> findByProcessProcessType(ProcessType processType);
}
