package org.zerock.projects.domain;


import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
//   프레스 , 차제 , 도장 , 조립  의 통합 테이블
@MappedSuperclass
@EntityListeners(value = {AbstractMethodError.class})
@Getter
public class BaseEntity {
    @CreatedDate
    @Column(name = "redate", updatable = false)
    private LocalDateTime reDate;


    @CreatedDate
    @Column(name = "moddate")
    private LocalDateTime modDate;
}
