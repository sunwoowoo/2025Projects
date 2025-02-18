package org.zerock.projects.repository;

import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;

import java.time.LocalDate;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class ProductionOrderRepositoryTests {
    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Test
    public void testOrderInsert() {
        Random random = new Random();
        IntStream.rangeClosed(1, 20).forEach(i -> {
            ProductionOrder order = ProductionOrder.builder()
                    .carModel("Model " + (char)('A' + i % 3))
                    .quantity(random.nextInt(50) + 1)
                    .orderStatus(OrderStatus.PENDING)
                    .processType(null)
                    .processes(null)
                    .progress(0.0)
                    .regDate(LocalDate.now())
                    .startDate(null)
                    .endDate(null)
                    .build();

            ProductionOrder result = productionOrderRepository.save(order);
            log.info("id: " + result.getId());
        });
    }
}
