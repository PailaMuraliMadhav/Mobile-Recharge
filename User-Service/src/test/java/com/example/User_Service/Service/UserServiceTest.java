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
        dummyUser.setEmail("murali@gmail.com");
        dummyUser.setName("Murali");
        dummyUser.setPassword("encodedPassword");
        dummyUser.setPhoneNumber("6300252614");
        dummyUser.setIsActive(true);
        dummyUser.setRole(Role.USER);

        dummyResponse = new UserResponse();
        dummyResponse.setId(1L);
        dummyResponse.setEmail("murali@gmail.com");
        dummyResponse.setName("Murali");
        dummyResponse.setPhoneNumber("6300252614");
    }

    @Test
    void testGetProfile_Success() {
        when(userRepository.findByEmailAndIsActiveTrue("murali@gmail.com"))
                .thenReturn(Optional.of(dummyUser));
        when(modelMapper.map(dummyUser, UserResponse.class))
                .thenReturn(dummyResponse);

        UserResponse result = userService.getProfile("murali@gmail.com");

        assertNotNull(result);
        assertEquals("Murali", result.getName());
        assertEquals("murali@gmail.com", result.getEmail());
        assertEquals("6300252614", result.getPhoneNumber());
        verify(userRepository, times(1))
                .findByEmailAndIsActiveTrue("murali@gmail.com");
    }

    @Test
    void testGetProfile_UserNotFound() {
        when(userRepository.findByEmailAndIsActiveTrue("notfound@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getProfile("notfound@gmail.com"));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("murali@gmail.com");
        request.setPhoneNumber("6300252614");
        request.setPassword("12345678");
        request.setName("Murali");

        User newUser = new User();
        newUser.setEmail("murali@gmail.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("murali@gmail.com");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber()))
                .thenReturn(false);
        when(modelMapper.map(request, User.class))
                .thenReturn(newUser);
        when(passwordEncoder.encode("12345678"))
                .thenReturn("encodedPassword");
        when(userRepository.save(newUser))
                .thenReturn(savedUser);

        UserResponse responseObj = new UserResponse();
        responseObj.setId(1L);
        responseObj.setEmail("murali@gmail.com");
        when(modelMapper.map(savedUser, UserResponse.class))
                .thenReturn(responseObj);

        UserResponse result = userService.register(request);

        assertNotNull(result);
        assertEquals("murali@gmail.com", result.getEmail());
        verify(userRepository).save(newUser);
    }

    @Test
    void testRegister_DuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("murali@gmail.com");
        request.setPhoneNumber("6300252614");
        request.setPassword("12345678");

        when(userRepository.existsByEmail("murali@gmail.com"))
                .thenReturn(true);

        assertThrows(DuplicateException.class,
                () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegister_DuplicatePhoneNumber() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("murali@gmail.com");
        request.setPhoneNumber("6300252614");
        request.setPassword("12345678");

        when(userRepository.existsByEmail("murali@gmail.com"))
                .thenReturn(false);
        when(userRepository.existsByPhoneNumber("6300252614"))
                .thenReturn(true);

        assertThrows(DuplicateException.class,
                () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("murali@gmail.com");
        request.setPassword("12345678");

        when(userRepository.findByEmailAndIsActiveTrue("murali@gmail.com"))
                .thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("12345678", "encodedPassword"))
                .thenReturn(true);
        when(jwtUtil.generateToken("murali@gmail.com", 1L, "USER"))
                .thenReturn("mocked-jwt-token");
        when(jwtUtil.getExpiration())
                .thenReturn(3600L);

        LoginResponse result = userService.login(request);

        assertNotNull(result);
        assertEquals("mocked-jwt-token", result.getToken());
        assertEquals("murali@gmail.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void testLogin_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("murali@gmail.com");
        request.setPassword("wrongpassword");

        when(userRepository.findByEmailAndIsActiveTrue("murali@gmail.com"))
                .thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword"))
                .thenReturn(false);

        assertThrows(InvalidDataException.class,
                () -> userService.login(request));
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@gmail.com");
        request.setPassword("12345678");

        when(userRepository.findByEmailAndIsActiveTrue("notfound@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.login(request));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(dummyUser));

        String result = userService.deleteUser(1L);

        assertEquals("User deleted successfully", result);
        assertFalse(dummyUser.getIsActive());
        verify(userRepository).save(dummyUser);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.deleteUser(99L));
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(dummyUser));
        when(modelMapper.map(dummyUser, UserResponse.class))
                .thenReturn(dummyResponse);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("murali@gmail.com", result.getEmail());
        assertEquals("6300252614", result.getPhoneNumber());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(99L));
    }
}
