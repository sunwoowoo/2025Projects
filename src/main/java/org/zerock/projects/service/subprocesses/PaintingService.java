package org.zerock.projects.service.subprocesses;

import org.zerock.projects.dto.PaintingDTO;

public interface PaintingService {
    Long register(PaintingDTO paintingDTO);
    PaintingDTO findPaintingByPno(Long pno);

    // 컬럼 업데이트
    void updatePainting(Long pno, boolean preprocessing, boolean electrodeposition,boolean sealing, boolean MTprocesses);
}
