package org.zerock.projects.repository;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.projects.domain.Assembly;

import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class AssemblyRepositoryTests {

    @Autowired
    private AssemblyRepository assemblyRepository;

    @Test
    public void AssmblyTestinsert() {
        IntStream.range(1, 10).forEach(i -> {
            Assembly assembly = Assembly.builder()
                    .AtE(i % 2 ==0)
                    .SM(i % 2 !=0)
                    .build();

            Assembly rr = assemblyRepository.save(assembly);
            log.info("ano" + rr.getAno());
        });
    }
}
