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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: AdminControllerTest
 * DESCRIPTION:
 *   Unit tests for OperatorService AdminController (operator and plan management).
 */
@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean OperatorService operatorService;

    @Test
    void addOperator_returns201() throws Exception {
        OperatorResponse resp = new OperatorResponse();
        resp.setId(1L); resp.setName("Jio");
        when(operatorService.addOperator(any())).thenReturn(resp);

        OperatorRequest req = new OperatorRequest();
        req.setName("Jio"); req.setCode("JIO");

        mockMvc.perform(post("/api/admin/operators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jio"));
    }

    @Test
    void updateOperator_returns200() throws Exception {
        OperatorResponse resp = new OperatorResponse();
        resp.setId(1L); resp.setName("Jio Updated");
        when(operatorService.updateOperator(eq(1L), any())).thenReturn(resp);

        OperatorRequest req = new OperatorRequest();
        req.setName("Jio Updated"); req.setCode("JIO");

        mockMvc.perform(put("/api/admin/operators/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jio Updated"));
    }

    @Test
    void deleteOperator_returns200() throws Exception {
        when(operatorService.deleteOperator(1L)).thenReturn("Operator deleted successfully");

        mockMvc.perform(delete("/api/admin/operators/1"))
                .andExpect(status().isOk());
    }

    @Test
    void addPlan_returns201() throws Exception {
        PlanResponse resp = new PlanResponse();
        resp.setId(10L); resp.setName("Basic Plan");
        when(operatorService.addPlan(eq(1L), any())).thenReturn(resp);

        PlanRequest req = new PlanRequest();
        req.setName("Basic Plan");
        req.setPrice(java.math.BigDecimal.valueOf(199));
        req.setValidityDays(28);
        req.setData("1GB/day");

        mockMvc.perform(post("/api/admin/operators/1/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Basic Plan"));
    }

    @Test
    void updatePlan_returns200() throws Exception {
        PlanResponse resp = new PlanResponse();
        resp.setId(10L); resp.setName("Updated Plan");
        when(operatorService.updatePlan(eq(10L), any())).thenReturn(resp);

        PlanRequest req = new PlanRequest();
        req.setName("Updated Plan");
        req.setPrice(java.math.BigDecimal.valueOf(299));
        req.setValidityDays(30);
        req.setData("2GB/day");

        mockMvc.perform(patch("/api/admin/operators/plans/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Plan"));
    }

    @Test
    void deletePlan_returns200() throws Exception {
        when(operatorService.deletePlan(10L)).thenReturn("Plan deleted successfully");

        mockMvc.perform(delete("/api/admin/operators/plans/10"))
                .andExpect(status().isOk());
    }
}
