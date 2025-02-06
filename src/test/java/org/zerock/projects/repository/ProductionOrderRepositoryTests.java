package org.zerock.projects.repository;

import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;

import java.time.LocalDate;

@SpringBootTest
@Log4j2
public class ProductionOrderRepositoryTests {
    @Autowired
    ProductionOrderRepository productionOrderRepository;

    @Test
    public void generateMockOrders() {
        Faker faker = new Faker();
        int n_orders = 20;
        for (int i = 0; i < n_orders; i++) {
            // order 객체 생성
            ProductionOrder order = new ProductionOrder();
            // 가상 오더 생성
            order.setCarModel(faker.options().option("Model S", "Model 3", "Model X", "Model Y"));
            order.setQuantity(faker.number().numberBetween(1, 100));
            order.setStartDate(LocalDate.now());
            order.setEndDate(LocalDate.now().plusDays(faker.number().numberBetween(1, 30)));
            order.setOrderStatus(faker.options().option(OrderStatus.PENDING, OrderStatus.IN_PROGRESS, OrderStatus.COMPLETED));
            productionOrderRepository.save(order);
        }
    }
}
