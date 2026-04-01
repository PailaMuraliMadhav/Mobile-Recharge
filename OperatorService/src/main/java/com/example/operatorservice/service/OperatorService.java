package com.example.operatorservice.service;

import com.example.operatorservice.dto.OperatorRequest;
import com.example.operatorservice.dto.OperatorResponse;
import com.example.operatorservice.dto.PlanRequest;
import com.example.operatorservice.dto.PlanResponse;
import com.example.operatorservice.entity.Operator;
import com.example.operatorservice.entity.Plan;
import com.example.operatorservice.exception.DuplicateException;
import com.example.operatorservice.exception.NotFoundException;
import com.example.operatorservice.repository.OperatorRepository;
import com.example.operatorservice.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository operatorRepository;
    private final PlanRepository planRepository;
    private final ModelMapper modelMapper;

    // Get all operators
    public List<OperatorResponse> getAllOperators() {

        return operatorRepository.findByIsActiveTrue()
                .stream()
                .map(operator -> modelMapper.map(operator, OperatorResponse.class))
                .toList();
    }

    // Get operator by id
    public OperatorResponse getOperatorById(Long id) {

        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Operator not found with id: " + id));

        return modelMapper.map(operator, OperatorResponse.class);
    }

    // Add operator
    @Transactional
    public OperatorResponse addOperator(OperatorRequest request) {

        if (operatorRepository.existsByName(request.getName())) {
            throw new DuplicateException(
                    "Operator already exists with name: " + request.getName());
        }

        Operator operator = modelMapper.map(request, Operator.class);
        operator.setIsActive(true);
        operator.setCode(request.getCode().toUpperCase());

        return modelMapper.map(operatorRepository.save(operator), OperatorResponse.class);
    }

    // Update operator
    @Transactional
    public OperatorResponse updateOperator(Long id, OperatorRequest request) {

        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Operator not found"));

        modelMapper.map(request, operator);
        operator.setIsActive(true);

        return modelMapper.map(operatorRepository.save(operator), OperatorResponse.class);
    }

    // Soft delete operator
    @Transactional
    public String deleteOperator(Long id) {

        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Operator not found"));

        operator.setIsActive(false);

        operatorRepository.save(operator);

        return "Operator deleted successfully";
    }

    // Get plans by operator
    public List<PlanResponse> getPlansByOperator(Long operatorId) {

        if (!operatorRepository.existsById(operatorId)) {
            throw new NotFoundException("Operator not found");
        }

        return planRepository
                .findByOperatorIdAndIsActiveTrue(operatorId)
                .stream()
                .map(plan -> modelMapper.map(plan, PlanResponse.class))
                .toList();
    }

    // Add plan
    @Transactional
    public PlanResponse addPlan(Long operatorId, PlanRequest request) {

        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new NotFoundException("Operator not found"));

        if (planRepository.existsByNameAndOperatorId(
                request.getName(), operatorId)) {

            throw new DuplicateException(
                    "Plan already exists for this operator");
        }

        Plan plan = modelMapper.map(request, Plan.class);

        plan.setOperator(operator);
        plan.setIsActive(true);

        return modelMapper.map(planRepository.save(plan), PlanResponse.class);
    }

    // Update plan
    @Transactional
    public PlanResponse updatePlan(Long planId, PlanRequest request) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        modelMapper.map(request, plan);

        plan.setIsActive(true);

        return modelMapper.map(planRepository.save(plan), PlanResponse.class);
    }

    // Soft delete plan
    @Transactional
    public String deletePlan(Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        plan.setIsActive(false);

        planRepository.save(plan);

        return "Plan deleted successfully";
    }
    public PlanResponse getPlanById(Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        return modelMapper.map(plan, PlanResponse.class);
    }
}