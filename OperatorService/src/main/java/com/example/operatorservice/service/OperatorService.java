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

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: OperatorService
 * DESCRIPTION:
 *   Business logic layer for managing telecom operators and their recharge plans.
 *   Supports CRUD operations for operators and plans, including soft-delete functionality.
 */
@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository operatorRepository;
    private final PlanRepository planRepository;
    private final ModelMapper modelMapper;

    /* ================================================================
     * METHOD: getAllOperators
     * DESCRIPTION:
     *   Returns a list of all currently active operators.
     * ================================================================ */
    public List<OperatorResponse> getAllOperators() {

        return operatorRepository.findByIsActiveTrue()
                .stream()
                .map(operator -> modelMapper.map(operator, OperatorResponse.class))
                .toList();
    }

    /* ================================================================
     * METHOD: getOperatorById
     * DESCRIPTION:
     *   Retrieves a single operator by its ID. Throws NotFoundException if not found.
     * ================================================================ */
    public OperatorResponse getOperatorById(Long id) {

        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Operator not found with id: " + id));

        return modelMapper.map(operator, OperatorResponse.class);
    }

    /* ================================================================
     * METHOD: addOperator
     * DESCRIPTION:
     *   Creates a new operator after verifying no duplicate name exists.
     *   Sets the operator as active and normalises the code to uppercase.
     * ================================================================ */
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

    /* ================================================================
     * METHOD: updateOperator
     * DESCRIPTION:
     *   Updates the details of an existing operator identified by its ID.
     *   Throws NotFoundException if the operator does not exist.
     * ================================================================ */
    @Transactional
    public OperatorResponse updateOperator(Long id, OperatorRequest request) {

        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Operator not found"));

        operator.setName(request.getName());
        operator.setCode(request.getCode().toUpperCase());
        operator.setDescription(request.getDescription());
        if (request.getLogoUrl() != null) {
            operator.setLogoUrl(request.getLogoUrl().isBlank() ? null : request.getLogoUrl());
        }
        operator.setIsActive(true);

        return modelMapper.map(operatorRepository.save(operator), OperatorResponse.class);
    }

    /* ================================================================
     * METHOD: deleteOperator
     * DESCRIPTION:
     *   Soft-deletes an operator by setting its active flag to false,
     *   preserving the record in the database.
     * ================================================================ */
    @Transactional
    public String deleteOperator(Long id) {

        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Operator not found"));

        operator.setIsActive(false);

        operatorRepository.save(operator);

        return "Operator deleted successfully";
    }

    /* ================================================================
     * METHOD: getPlansByOperator
     * DESCRIPTION:
     *   Returns all active recharge plans associated with the given operator ID.
     *   Throws NotFoundException if the operator does not exist.
     * ================================================================ */
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

    /* ================================================================
     * METHOD: addPlan
     * DESCRIPTION:
     *   Adds a new recharge plan to the specified operator after checking for
     *   duplicate plan names within the same operator.
     * ================================================================ */
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

    /* ================================================================
     * METHOD: updatePlan
     * DESCRIPTION:
     *   Updates the details of an existing recharge plan identified by its plan ID.
     *   Throws NotFoundException if the plan does not exist.
     * ================================================================ */
    @Transactional
    public PlanResponse updatePlan(Long planId, PlanRequest request) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        modelMapper.map(request, plan);

        plan.setIsActive(true);

        return modelMapper.map(planRepository.save(plan), PlanResponse.class);
    }

    /* ================================================================
     * METHOD: deletePlan
     * DESCRIPTION:
     *   Soft-deletes a recharge plan by setting its active flag to false,
     *   preserving the record in the database.
     * ================================================================ */
    @Transactional
    public String deletePlan(Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        plan.setIsActive(false);

        planRepository.save(plan);

        return "Plan deleted successfully";
    }

    /* ================================================================
     * METHOD: getPlanById
     * DESCRIPTION:
     *   Retrieves a single recharge plan by its ID. Throws NotFoundException if not found.
     * ================================================================ */
    public PlanResponse getPlanById(Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        return modelMapper.map(plan, PlanResponse.class);
    }
}
