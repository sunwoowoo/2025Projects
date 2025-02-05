package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.Assembly;

public interface AssemblyRepository extends JpaRepository<Assembly, Long> {
}
