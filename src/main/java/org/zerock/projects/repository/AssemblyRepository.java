package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.projects.domain.Assembly;


//  조립  테이블
public interface AssemblyRepository extends JpaRepository<Assembly, Long> {

}
