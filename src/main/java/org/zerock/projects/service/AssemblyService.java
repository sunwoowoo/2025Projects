package org.zerock.projects.service;

import org.zerock.projects.dto.AssemblyDTO;
// 조립 테이블
public interface AssemblyService {
    Long register(AssemblyDTO assemblyDTO);  // Assembly 등록
    AssemblyDTO findAssemblyByAno(Long ano);

    // 엔진 조립 및 시트 장착 여부 업데이트
    void updateAssembly(Long ano, boolean atE, boolean sm);
}
