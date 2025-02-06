package org.zerock.projects.service.subprocesses;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.subprocesses.Assembly;
import org.zerock.projects.domain.Board;
import org.zerock.projects.dto.AssemblyDTO;
import org.zerock.projects.repository.subprocesses.AssemblyRepository;
import org.zerock.projects.repository.BoardRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AssemblyServiceImpl implements AssemblyService {

    private final ModelMapper modelMapper;
    private final AssemblyRepository assemblyRepository;
    private final BoardRepository boardRespository;

    @Override
    public Long register(AssemblyDTO assemblyDTO) {
        Assembly assembly = modelMapper.map(assemblyDTO, Assembly.class);
        Assembly savedAssembly = assemblyRepository.save(assembly);
        return savedAssembly.getAno();
    }
//    조립 테이블 ano 조회
    @Override
    public AssemblyDTO findAssemblyByAno(Long ano) {
        Optional<Assembly> result = assemblyRepository.findById(ano);
        return result.map(assembly -> modelMapper.map(assembly, AssemblyDTO.class)).orElse(null);
    }
//    조립 컬럼 실시간 수정
    @Transactional
    @Override
    public void updateAssembly(Long ano, boolean atE, boolean sm) {

      Assembly assembly = assemblyRepository.findById(ano).orElseThrow(() -> new IllegalArgumentException("Assembly not found : " + ano));
      assembly.setAtE(atE);
      assembly.setSM(sm);
      assemblyRepository.save(assembly);

        List<Board> boardList = boardRespository.findAllByAssembly(assembly);
        for (Board board : boardList) {
            board.updateProgress();
            boardRespository.save(board);
        }
    }
}
