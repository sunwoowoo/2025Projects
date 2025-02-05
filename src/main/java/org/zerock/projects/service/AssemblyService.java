package org.zerock.projects.service;

import org.zerock.projects.dto.AssemblyDTO;
// 조립 테이블
public interface AssemblyService {
    Long register(AssemblyDTO assemblyDTO);
}
