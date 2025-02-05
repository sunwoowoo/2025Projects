package org.zerock.projects.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.Assembly;
import org.zerock.projects.dto.AssemblyDTO;
import org.zerock.projects.repository.AssemblyRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AssemblyServiceImpl implements  AssemblyService  {

    private final ModelMapper modelMapper;
    private final AssemblyRepository assemblyRepository;

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
//    조립 테이블 실시간 수정
    @Transactional
    @Override
    public void updateAssembly(Long ano, boolean atE, boolean sm) {

        Optional<Assembly> optionalAssembly = assemblyRepository.findById(ano);
        if(optionalAssembly.isPresent()){
            Assembly assembly = optionalAssembly.get();
            assembly.setAtE(atE);
            assembly.setSM(sm);
            assemblyRepository.save(assembly);
        }
        else {
            throw new IllegalArgumentException("해당 ano의 Assembly 를 찾을 수 없다");
        }
    }
}
