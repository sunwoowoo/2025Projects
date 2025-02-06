package org.zerock.projects.service.subprocesses;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.projects.domain.Board;
import org.zerock.projects.domain.subprocesses.Painting;
import org.zerock.projects.dto.PaintingDTO;
import org.zerock.projects.repository.BoardRepository;
import org.zerock.projects.repository.subprocesses.PaintingRepository;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class PaintingServiceImpl implements PaintingService {

    private final ModelMapper modelMapper;
    private final PaintingRepository paintingRepository;
    private final BoardRepository boardRespository;

    @Override
    public Long register(PaintingDTO paintingDTO){
        Painting painting = modelMapper.map(paintingDTO, Painting.class);
        Painting savedPainting = paintingRepository.save(painting);
        return savedPainting.getPno();
    }
    @Override
    public PaintingDTO  findPaintingByPno(Long pno){
        Optional<Painting> result = paintingRepository.findById(pno);
        return result.map(painting -> modelMapper.map(painting, PaintingDTO.class)).orElse(null);
    }

//    도장 컬럼 실시간 수정
    @Transactional
    @Override
    public void updatePainting(Long pno,  boolean preprocessing, boolean electrodeposition, boolean sealing , boolean MTprocesses){
        Painting painting = paintingRepository.findById(pno).orElseThrow(() -> new IllegalArgumentException("No such painting"));

        painting.setPreprocessing(preprocessing);
        painting.setElectrodeposition(electrodeposition);
        painting.setSealing(sealing);
        painting.setMTprocesses(MTprocesses);

        paintingRepository.save(painting);

        List<Board> boardList = boardRespository.findAllByPainting(painting);
        for (Board board : boardList) {
            board.updateProgress();
            boardRespository.save(board);
        }
    }
}
