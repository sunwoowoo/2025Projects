package org.zerock.projects.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;
import org.zerock.projects.repository.ProductionOrderRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionOrderSearch {

    private final ProductionOrderRepository productionOrderRepository;

    // 특정 차량 모델, 공정 타입, 진행률을 기준으로 조회
    public List<ProductionOrder> searchByCarModelProcessTypeProgress(String carModel, ProcessType processType, double progress) {
        return productionOrderRepository.findByCarModelAndProcessTypeAndProgress(carModel, processType, progress);
    }

    // 여러 개의 차량 모델을 기준으로 조회
    public List<ProductionOrder> searchByCarModels(List<String> carModels) {
        return productionOrderRepository.findByCarModelIn(carModels);
    }

    // 특정 키워드가 포함된 차량 모델을 검색하고, OrderId 기준으로 내림차순 정렬 (페이징 포함)
    public Page<ProductionOrder> searchByCarModelKeyword(String keyword, Pageable pageable) {
        return productionOrderRepository.findByCarModelContainingOrderByIdDesc(keyword, pageable);
    }

    // 특정 차량 모델과 공정 타입을 기준으로 JPQL 조회
    public List<ProductionOrder> searchFromCarModelProcessType(String carModel, ProcessType processType) {
        return productionOrderRepository.findFromCarModelProcessType(carModel, processType);
    }

    // 특정 공정 타입에 포함된 주문을 조회 (IN 조건 사용)
    public Page<ProductionOrder> searchFromProcessTypes(List<ProcessType> processTypes, Pageable pageable) {
        return productionOrderRepository.findFromProcessTypes(processTypes, pageable);  // 쿼리 메소드 호출
    }

    // 특정 키워드가 포함된 차량 모델을 검색 (LIKE 사용, 페이징 포함)
    public Page<ProductionOrder> searchByKeyword(String keyword, Pageable pageable) {
        return productionOrderRepository.findKeyword(keyword, pageable);
    }

    // 주문 현황에 따라 검색
    public Page<ProductionOrder> searchByStatus(List<OrderStatus> orderStatuses, Pageable pageable) {
        return productionOrderRepository.findByOrderStatus(orderStatuses, pageable);
    }

    // 등록 날짜 오름차순으로 정렬(검색)
    public Page<ProductionOrder> findAllOrderByRegDateAsc(Pageable pageable) {
        return productionOrderRepository.findAllOrderByRegDateAsc(pageable);
    }

    // 등록 날짜에 따라 검색
    public Page<ProductionOrder> findByRegDate(LocalDate date, Pageable pageable) {
        return productionOrderRepository.findByRegDate(date, pageable);
    }

    // OrderStatus 별 차량 모델 검색
    public Page<ProductionOrder> findByKeywordAndStatus(String keyword, List<OrderStatus> statuses, Pageable pageable) {
        return productionOrderRepository.findByKeywordAndStatus(keyword, statuses, pageable);
    };

    // OrderStatus 별 제품 등록 날짜로 검색
    public Page<ProductionOrder> findByRegDateAndStatus(LocalDate regDate, List<OrderStatus> statuses, Pageable pageable) {
        return productionOrderRepository.findByRegDateAndStatus(regDate, statuses, pageable);
    };

    // OrderStatus 별 정렬
    public Page<ProductionOrder> sortByOrderStatus(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productionOrderRepository.findAllOrderedByOrderStatus(pageable);
    }
}
