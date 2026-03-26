package com.example.User_Service.Service;

import com.example.User_Service.Dto.*;
import com.example.User_Service.Entity.User;
import com.example.User_Service.Enums.Role;
import com.example.User_Service.Exceptions.DuplicateException;
import com.example.User_Service.Exceptions.InvalidDataException;
import com.example.User_Service.Exceptions.NotFoundException;
import com.example.User_Service.Repository.UserRepository;
import com.example.User_Service.Security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User dummyUser;
    private UserResponse dummyResponse;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setEmail("test@example.com");
        dummyUser.setName("Test User");
        dummyUser.setPassword("encodedPassword");
        dummyUser.setPhoneNumber("1234567890");
        dummyUser.setIsActive(true);
        dummyUser.setRole(Role.USER);

        dummyResponse = new UserResponse();
        dummyResponse.setId(1L);
        dummyResponse.setEmail("test@example.com");
        dummyResponse.setName("Test User");
        dummyResponse.setPhoneNumber("1234567890");
    }

    @Test
    void testGetProfile_Success() {
        when(userRepository.findByEmailAndIsActiveTrue("test@example.com")).thenReturn(Optional.of(dummyUser));
        when(modelMapper.map(dummyUser, UserResponse.class)).thenReturn(dummyResponse);

        UserResponse result = userService.getProfile("test@example.com");

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findByEmailAndIsActiveTrue("test@example.com");
    }

    @Test
    void testGetProfile_UserNotFound() {
        when(userRepository.findByEmailAndIsActiveTrue("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getProfile("notfound@example.com"));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPhoneNumber("9876543210");
        request.setPassword("password123");

        User newUser = new User();
        newUser.setEmail("newuser@example.com");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setEmail("newuser@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(modelMapper.map(request, User.class)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        
        UserResponse responseObj = new UserResponse();
        responseObj.setId(2L);
        when(modelMapper.map(savedUser, UserResponse.class)).thenReturn(responseObj);

        UserResponse result = userService.register(request);

        assertNotNull(result);
        verify(userRepository).save(newUser);
    }

    @Test
    void testRegister_DuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DuplicateException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmailAndIsActiveTrue("test@example.com")).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", 1L, "USER")).thenReturn("mocked-jwt-token");
        when(jwtUtil.getExpiration()).thenReturn(3600L);

        LoginResponse result = userService.login(request);

        assertNotNull(result);
        assertEquals("mocked-jwt-token", result.getToken());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testLogin_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(userRepository.findByEmailAndIsActiveTrue("test@example.com")).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidDataException.class, () -> userService.login(request));
    }
}
