package com.example.OperatorService.Repository;

import com.example.OperatorService.Entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository  extends JpaRepository<Plan,Long> {

    List<Plan> findByOperatorId(Long operatorId);

    List<Plan> findByOperatorIdAndIsActiveTrue(Long operatorId);

    boolean existsByNameAndOperatorId(String name, Long operatorId);
    boolean existsByNameAndOperatorIdAndIdNot(String name, Long operatorId, Long id);

}
