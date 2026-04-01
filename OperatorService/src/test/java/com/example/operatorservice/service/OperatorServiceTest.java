package com.example.operatorservice.service;

import com.example.operatorservice.dto.*;
import com.example.operatorservice.entity.*;
import com.example.operatorservice.exception.NotFoundException;
import com.example.operatorservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Fixes the PotentialStubbingProblem
class OperatorServiceTest {

    @Mock private OperatorRepository operatorRepository;
    @Mock private PlanRepository planRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private OperatorService operatorService;

    private Operator dummyOperator;
    private Plan dummyPlan;
    private OperatorResponse dummyOpResponse;
    private PlanResponse dummyPlanResponse;

    @BeforeEach
    void setUp() {
        dummyOperator = new Operator();
        dummyOperator.setId(1L);
        dummyOperator.setName("Jio");
        dummyOperator.setCode("JIO");
        dummyOperator.setIsActive(true);

        dummyPlan = new Plan();
        dummyPlan.setId(10L);
        dummyPlan.setName("Basic Plan");
        dummyPlan.setOperator(dummyOperator);
        dummyPlan.setIsActive(true);

        dummyOpResponse = new OperatorResponse();
        dummyOpResponse.setId(1L);
        dummyOpResponse.setName("Jio");

        dummyPlanResponse = new PlanResponse();
        dummyPlanResponse.setId(10L);
    }

    // ================= OPERATOR TESTS =================

    @Test
    void testGetAllOperators() {
        when(operatorRepository.findByIsActiveTrue()).thenReturn(List.of(dummyOperator));
        when(modelMapper.map(any(Operator.class), eq(OperatorResponse.class))).thenReturn(dummyOpResponse);

        List<OperatorResponse> result = operatorService.getAllOperators();
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetOperatorById_Success() {
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(dummyOperator));
        when(modelMapper.map(any(Operator.class), eq(OperatorResponse.class))).thenReturn(dummyOpResponse);

        assertNotNull(operatorService.getOperatorById(1L));
    }

    @Test
    void testAddOperator_Success() {
        OperatorRequest req = new OperatorRequest();
        req.setName("Airtel");
        req.setCode("air");

        when(operatorRepository.existsByName(anyString())).thenReturn(false);
        when(modelMapper.map(any(OperatorRequest.class), eq(Operator.class))).thenReturn(dummyOperator);
        when(operatorRepository.save(any(Operator.class))).thenReturn(dummyOperator);
        when(modelMapper.map(any(Operator.class), eq(OperatorResponse.class))).thenReturn(dummyOpResponse);

        assertNotNull(operatorService.addOperator(req));
    }

    @Test
    void testUpdateOperator_Success() {
        OperatorRequest req = new OperatorRequest();
        req.setName("Jio Updated");

        // Ensure the ID exists
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(dummyOperator));

        // This handles the void map(request, existingEntity) call
        doNothing().when(modelMapper).map(any(OperatorRequest.class), any(Operator.class));

        when(operatorRepository.save(any(Operator.class))).thenReturn(dummyOperator);
        when(modelMapper.map(any(Operator.class), eq(OperatorResponse.class))).thenReturn(dummyOpResponse);

        assertNotNull(operatorService.updateOperator(1L, req));
    }

    @Test
    void testDeleteOperator_Success() {
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(dummyOperator));
        String res = operatorService.deleteOperator(1L);
        assertFalse(dummyOperator.getIsActive());
        assertEquals("Operator deleted successfully", res);
    }

    // ================= PLAN TESTS =================

    @Test
    void testAddPlan_Success() {
        PlanRequest req = new PlanRequest();
        req.setName("New Plan");

        when(operatorRepository.findById(1L)).thenReturn(Optional.of(dummyOperator));
        when(planRepository.existsByNameAndOperatorId(anyString(), anyLong())).thenReturn(false);
        when(modelMapper.map(any(PlanRequest.class), eq(Plan.class))).thenReturn(dummyPlan);
        when(planRepository.save(any(Plan.class))).thenReturn(dummyPlan);
        when(modelMapper.map(any(Plan.class), eq(PlanResponse.class))).thenReturn(dummyPlanResponse);

        assertNotNull(operatorService.addPlan(1L, req));
    }

    @Test
    void testUpdatePlan_Success() {
        PlanRequest req = new PlanRequest();

        when(planRepository.findById(10L)).thenReturn(Optional.of(dummyPlan));

        // Void mapping handle
        doNothing().when(modelMapper).map(any(PlanRequest.class), any(Plan.class));

        when(planRepository.save(any(Plan.class))).thenReturn(dummyPlan);
        when(modelMapper.map(any(Plan.class), eq(PlanResponse.class))).thenReturn(dummyPlanResponse);

        assertNotNull(operatorService.updatePlan(10L, req));
    }

    @Test
    void testGetPlanById_Success() {
        when(planRepository.findById(10L)).thenReturn(Optional.of(dummyPlan));
        when(modelMapper.map(any(Plan.class), eq(PlanResponse.class))).thenReturn(dummyPlanResponse);

        assertNotNull(operatorService.getPlanById(10L));
    }
    // --- Add these inside OperatorServiceTest.java ---


    @Test
    void testDeleteOperator_NotFound() {
        when(operatorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> operatorService.deleteOperator(99L));
    }

    @Test
    void testGetPlansByOperator_NotFound() {
        when(operatorRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> operatorService.getPlansByOperator(99L));
    }



    @Test
    void testDeletePlan_Success() {
        when(planRepository.findById(10L)).thenReturn(Optional.of(dummyPlan));
        String res = operatorService.deletePlan(10L);
        assertFalse(dummyPlan.getIsActive());
        assertEquals("Plan deleted successfully", res);
    }

    @Test
    void testDeletePlan_NotFound() {
        when(planRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> operatorService.deletePlan(99L));
    }

    @Test
    void testGetPlanById_NotFound() {
        when(planRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> operatorService.getPlanById(99L));
    }
    @Test
    void testUpdateOperator_NotFound() {
        when(operatorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // FIX: Instantiate OUTSIDE the lambda
        OperatorRequest request = new OperatorRequest();

        assertThrows(NotFoundException.class, () -> operatorService.updateOperator(99L, request));
    }

    @Test
    void testAddPlan_OperatorNotFound() {
        when(operatorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // FIX: Instantiate OUTSIDE the lambda
        PlanRequest request = new PlanRequest();

        assertThrows(NotFoundException.class, () -> operatorService.addPlan(99L, request));
    }

    @Test
    void testUpdatePlan_NotFound() {
        when(planRepository.findById(anyLong())).thenReturn(Optional.empty());

        // FIX: Instantiate OUTSIDE the lambda
        PlanRequest request = new PlanRequest();

        assertThrows(NotFoundException.class, () -> operatorService.updatePlan(99L, request));
    }
}