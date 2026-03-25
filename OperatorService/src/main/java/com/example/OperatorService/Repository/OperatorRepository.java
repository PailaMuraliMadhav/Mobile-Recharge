package com.example.OperatorService.Repository;

import com.example.OperatorService.Entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator,Long> {
    boolean existsByName(String name);
    List<Operator> findByIsActiveTrue();
}
