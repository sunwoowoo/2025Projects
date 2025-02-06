package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.Painting;

public interface PaintingRepository  extends JpaRepository<Painting, Long> {

}
