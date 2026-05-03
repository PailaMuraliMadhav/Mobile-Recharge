package com.example.operatorservice.controller;

import com.example.operatorservice.dto.*;
import com.example.operatorservice.service.OperatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: OperatorControllerTest
 * DESCRIPTION:
 *   Unit tests for OperatorController (public read-only endpoints).
 */
@WebMvcTest(OperatorController.class)
@AutoConfigureMockMvc(addFilters = false)
class OperatorControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean OperatorService operatorService;

    @Test
    void getAllOperators_returns200() throws Exception {
        OperatorResponse op = new OperatorResponse();
        op.setId(1L); op.setName("Jio");
        when(operatorService.getAllOperators()).thenReturn(List.of(op));

        mockMvc.perform(get("/api/operators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Jio"));
    }

    @Test
    void getOperatorById_returns200() throws Exception {
        OperatorResponse op = new OperatorResponse();
        op.setId(1L); op.setName("Airtel");
        when(operatorService.getOperatorById(1L)).thenReturn(op);

        mockMvc.perform(get("/api/operators/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Airtel"));
    }

    @Test
    void getPlansByOperator_returns200() throws Exception {
        PlanResponse plan = new PlanResponse();
        plan.setId(10L); plan.setName("Basic");
        when(operatorService.getPlansByOperator(1L)).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/operators/1/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Basic"));
    }

    @Test
    void getPlanById_returns200() throws Exception {
        PlanResponse plan = new PlanResponse();
        plan.setId(10L); plan.setName("Premium");
        when(operatorService.getPlanById(10L)).thenReturn(plan);

        mockMvc.perform(get("/api/operators/plans/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Premium"));
    }
}
