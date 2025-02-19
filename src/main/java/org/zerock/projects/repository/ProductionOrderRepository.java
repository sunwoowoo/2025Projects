package org.zerock.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.projects.domain.OrderStatus;
import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.ProcessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    //  특정 차량 모델, 공정 타입, 진행률을 기준으로 조회
    List<ProductionOrder> findByCarModelAndProcessTypeAndProgress(String carModel, ProcessType processType, double progress);

    //  여러 개의 차량 모델을 기준으로 조회
    List<ProductionOrder> findByCarModelIn(List<String> carModels);

    //  특정 키워드가 포함된 차량 모델을 검색하고, OrderId 기준으로 내림차순 정렬
    Page<ProductionOrder> findByCarModelContainingOrderByIdDesc(String keyword, Pageable pageable);

    //  특정 차량 모델과 공정 타입을 기준으로 JPQL 조회
    @Query("SELECT b FROM ProductionOrder b WHERE b.carModel = :carModel AND b.processType = :processType")
    List<ProductionOrder> findFromCarModelProcessType(@Param("carModel") String carModel, @Param("processType") ProcessType processType);

    //  특정 공정 타입에 포함된 주문을 조회 (IN 조건 사용)
    @Query("SELECT b FROM ProductionOrder b WHERE b.processType IN :processTypes")
    Page<ProductionOrder> findFromProcessTypes(@Param("processTypes") List<ProcessType> processTypes, Pageable pageable);

    //  특정 키워드가 포함된 차량 모델을 검색 (LIKE 사용)
    @Query("SELECT b FROM ProductionOrder b WHERE b.carModel LIKE CONCAT('%', :keyword, '%')")
    Page<ProductionOrder> findKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 주문 상태에 따라 검색
    @Query("SELECT b FROM ProductionOrder b WHERE b.orderStatus IN :orderStatus")
    Page<ProductionOrder> findByOrderStatus(@Param("orderStatus") List<OrderStatus> orderStatus, Pageable pageable);

    @Query("SELECT b FROM ProductionOrder b WHERE b.orderStatus IN :orderStatus")
    List<ProductionOrder> findByOrderStatusInList(@Param("orderStatus") List<OrderStatus> orderStatus);

    // 등록 날짜 오름차순으로 정렬
    @Query("SELECT b FROM ProductionOrder b ORDER BY b.regDate ASC")
    Page<ProductionOrder> findAllOrderByRegDateAsc(Pageable pageable);

    // 등록 날짜별로 검색
    @Query("SELECT b FROM ProductionOrder b WHERE DATE(b.regDate) = DATE(:date) ORDER BY b.regDate ASC")
    Page<ProductionOrder> findByRegDate(@Param("date") LocalDate date, Pageable pageable);

    void deleteByProcesses_Id(Long processId);

    // 특정 OrderStatus가 포함된 주문 고르기
    Page<ProductionOrder> findByOrderStatusIn(List<OrderStatus> statuses, Pageable pageable);

    // OrderStatus 별 차량 모델 검색
    @Query("SELECT b FROM ProductionOrder b WHERE b.carModel LIKE CONCAT('%', :keyword, '%') AND b.orderStatus IN :orderStatuses")
    Page<ProductionOrder> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("orderStatuses") List<OrderStatus> orderStatuses, Pageable pageable);

    // OrderStatus 별 제품 등록 날짜로 검색
    @Query("SELECT b FROM ProductionOrder b WHERE DATE(b.regDate) = DATE(:date) AND b.orderStatus IN :orderStatuses ORDER BY b.regDate ASC")
    Page<ProductionOrder> findByRegDateAndStatus(@Param("date") LocalDate date, @Param("orderStatuses") List<OrderStatus> orderStatuses, Pageable pageable);
}
