package com.example.OperatorService.Service;

import com.example.OperatorService.Dto.*;
import com.example.OperatorService.Entity.*;
import com.example.OperatorService.Exception.*;
import com.example.OperatorService.Repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository operatorRepository;
    private final PlanRepository planRepository;
    private final ModelMapper modelMapper;

    // --- OPERATOR LOGIC ---

    public List<OperatorResponse> getAllOperators() {
        return operatorRepository.findByIsActiveTrue().stream()
                .map(op -> modelMapper.map(op, OperatorResponse.class))
                .collect(Collectors.toList());
    }

    public OperatorResponse getOperatorById(Long id) {
        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Operator not found with id: " + id));
        return modelMapper.map(operator, OperatorResponse.class);
    }

    @Transactional
    public OperatorResponse addOperator(OperatorRequest request) {
        if (operatorRepository.existsByName(request.getName())) {
            throw new DuplicateException("Operator already exists with name: " + request.getName());
        }

        Operator operator = modelMapper.map(request, Operator.class);
        operator.setIsActive(true);
        operator.setCode(request.getCode().toUpperCase());

        return modelMapper.map(operatorRepository.save(operator), OperatorResponse.class);
    }

    @Transactional
    public OperatorResponse updateOperator(Long id, OperatorRequest request) {
        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Operator not found"));

        modelMapper.map(request, operator);
        return modelMapper.map(operatorRepository.save(operator), OperatorResponse.class);
    }

    @Transactional
    public String deleteOperator(Long id) {
        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Operator not found"));
        operator.setIsActive(false); // Soft Delete
        operatorRepository.save(operator);
        return "Operator deleted successfully";
    }

    // --- PLAN LOGIC ---

    public List<PlanResponse> getPlansByOperator(Long operatorId) {
        if (!operatorRepository.existsById(operatorId)) {
            throw new NotFoundException("Operator not found");
        }
        return planRepository.findByOperatorIdAndIsActiveTrue(operatorId).stream()
                .map(plan -> modelMapper.map(plan, PlanResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanResponse addPlan(Long operatorId, PlanRequest request) {
        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new NotFoundException("Operator not found"));

        if (planRepository.existsByNameAndOperatorId(request.getName(), operatorId)) {
            throw new DuplicateException("Plan already exists for this operator");
        }

        Plan plan = modelMapper.map(request, Plan.class);
        plan.setOperator(operator);
        plan.setIsActive(true);

        return modelMapper.map(planRepository.save(plan), PlanResponse.class);
    }

    @Transactional
    public PlanResponse updatePlan(Long planId, PlanRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        plan.setIsActive(true);
        modelMapper.map(request, plan);
        return modelMapper.map(planRepository.save(plan), PlanResponse.class);
    }

    @Transactional
    public String deletePlan(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));
        plan.setIsActive(false);
        planRepository.save(plan);
        return "Plan deleted successfully";
    }
}