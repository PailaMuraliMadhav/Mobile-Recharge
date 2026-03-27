package com.example.OperatorService.Service;

import com.example.OperatorService.Dto.*;
import com.example.OperatorService.Entity.*;
import com.example.OperatorService.Exception.DuplicateException;
import com.example.OperatorService.Exception.NotFoundException;
import com.example.OperatorService.Repository.OperatorRepository;
import com.example.OperatorService.Repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperatorServiceTest {

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OperatorService operatorService;

    private Operator dummyOperator;
    private OperatorResponse dummyResponse;

    @BeforeEach
    void setUp() {
        dummyOperator = new Operator();
        dummyOperator.setId(1L);
        dummyOperator.setName("Jio");
        dummyOperator.setCode("JIO");
        dummyOperator.setIsActive(true);

        dummyResponse = new OperatorResponse();
        dummyResponse.setId(1L);
        dummyResponse.setName("Jio");
        dummyResponse.setCode("JIO");
    }

    @Test
    void testGetAllOperators() {
        when(operatorRepository.findByIsActiveTrue()).thenReturn(List.of(dummyOperator));
        when(modelMapper.map(dummyOperator, OperatorResponse.class)).thenReturn(dummyResponse);

        List<OperatorResponse> result = operatorService.getAllOperators();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jio", result.get(0).getName());
        verify(operatorRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testGetOperatorById_Success() {
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(dummyOperator));
        when(modelMapper.map(dummyOperator, OperatorResponse.class)).thenReturn(dummyResponse);

        OperatorResponse result = operatorService.getOperatorById(1L);

        assertNotNull(result);
        assertEquals("Jio", result.getName());
    }

    @Test
    void testGetOperatorById_NotFound() {
        when(operatorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> operatorService.getOperatorById(99L));
    }

    @Test
    void testAddOperator_Success() {
        OperatorRequest request = new OperatorRequest();
        request.setName("Airtel");
        request.setCode("AIR");

        Operator newOperator = new Operator();
        newOperator.setName("Airtel");
        newOperator.setCode("AIR");

        Operator savedOperator = new Operator();
        savedOperator.setId(2L);
        savedOperator.setName("Airtel");
        savedOperator.setCode("AIR");
        savedOperator.setIsActive(true);

        OperatorResponse response = new OperatorResponse();
        response.setId(2L);
        response.setName("Airtel");

        when(operatorRepository.existsByName("Airtel")).thenReturn(false);
        when(modelMapper.map(request, Operator.class)).thenReturn(newOperator);
        when(operatorRepository.save(any(Operator.class))).thenReturn(savedOperator);
        when(modelMapper.map(savedOperator, OperatorResponse.class)).thenReturn(response);

        OperatorResponse result = operatorService.addOperator(request);

        assertNotNull(result);
        assertEquals("Airtel", result.getName());
        verify(operatorRepository).save(any(Operator.class));
    }

    @Test
    void testAddOperator_Duplicate() {
        OperatorRequest request = new OperatorRequest();
        request.setName("Jio");

        when(operatorRepository.existsByName("Jio")).thenReturn(true);

        assertThrows(DuplicateException.class, () -> operatorService.addOperator(request));
        verify(operatorRepository, never()).save(any());
    }

    @Test
    void testDeleteOperator_Success() {
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(dummyOperator));

        String result = operatorService.deleteOperator(1L);

        assertEquals("Operator deleted successfully", result);
        assertFalse(dummyOperator.getIsActive());
        verify(operatorRepository).save(dummyOperator);
    }
}
