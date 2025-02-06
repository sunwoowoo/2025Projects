package org.zerock.projects.repository.subprocesses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.subprocesses.Painting;

public interface PaintingRepository  extends JpaRepository<Painting, Long> {

}
