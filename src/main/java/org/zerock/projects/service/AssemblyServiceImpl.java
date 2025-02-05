package org.zerock.projects.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.Assembly;
import org.zerock.projects.dto.AssemblyDTO;
import org.zerock.projects.repository.AssemblyRepository;

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
}
