package org.zerock.projects.repository.machines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.projects.domain.machines.Process;
import org.zerock.projects.domain.machines.ProcessType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {
    // 공정단계별로 찾기 (프레싱, 차체용접, 도장, 조립)
    Optional<Process> findByProcessType(ProcessType processType);
}
