package com.example.userservice.service;

import com.example.userservice.dto.*;
import com.example.userservice.entity.User;
import com.example.userservice.enums.Role;
import com.example.userservice.exceptions.DuplicateException;
import com.example.userservice.exceptions.InvalidDataException;
import com.example.userservice.exceptions.NotFoundException;
import com.example.userservice.feignclients.PaymentClient;
import com.example.userservice.feignclients.RechargeClient;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtil;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private RechargeClient rechargeClient;
    @Mock private PaymentClient paymentClient;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse response;

    @BeforeEach
    void setup() {

        user = new User();
        user.setId(1L);
        user.setEmail("murali@gmail.com");
        user.setPassword("encoded");
        user.setPhoneNumber("9999999999");
        user.setIsActive(true);
        user.setRole(Role.USER);

        response = new UserResponse();
        response.setId(1L);
        response.setEmail("murali@gmail.com");
    }

    // REGISTER SUCCESS
    @Test
    void register_success() {

        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@gmail.com");
        req.setPhoneNumber("8888888888");
        req.setPassword("pass");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(modelMapper.map(any(), eq(User.class))).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(response);

        UserResponse result = userService.register(req);

        assertNotNull(result);
    }

    // REGISTER EMAIL DUPLICATE
    @Test
    void register_emailDuplicate() {

        RegisterRequest req = new RegisterRequest();
        req.setEmail("murali@gmail.com");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(DuplicateException.class,
                () -> userService.register(req));
    }
    @Test
    void updateProfile_phoneDuplicate() {

        UpdateProfile req = new UpdateProfile();
        req.setPhoneNumber("9999999999");

        when(userRepository.findByEmailAndIsActiveTrue(any()))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByPhoneNumberAndIdNot(any(), any()))
                .thenReturn(true);

        assertThrows(DuplicateException.class,
                () -> userService.updateProfile("murali@gmail.com", req));
    }
    @Test
    void permanentDeleteUser_success() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        String result = userService.permanentDeleteUser(1L);

        assertEquals("User with id 1 has been permanently deleted", result);
    }

    // REGISTER PHONE DUPLICATE
    @Test
    void register_phoneDuplicate() {

        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@gmail.com");
        req.setPhoneNumber("9999999999");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(true);

        assertThrows(DuplicateException.class,
                () -> userService.register(req));
    }

    // LOGIN SUCCESS
    @Test
    void login_success() {

        LoginRequest req = new LoginRequest();
        req.setEmail("murali@gmail.com");
        req.setPassword("pass");

        when(userRepository.findByEmailAndIsActiveTrue(any()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        when(jwtUtil.generateToken(any(), anyLong(), any()))
                .thenReturn("token");

        when(jwtUtil.getExpiration()).thenReturn(86400000L);

        LoginResponse res = userService.login(req);

        assertEquals("token", res.getToken());
    }

    // LOGIN USER NOT FOUND
    @Test
    void login_userNotFound() {

        LoginRequest req = new LoginRequest();
        req.setEmail("unknown@gmail.com");

        when(userRepository.findByEmailAndIsActiveTrue(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.login(req));
    }

    // LOGIN INVALID PASSWORD
    @Test
    void login_invalidPassword() {

        LoginRequest req = new LoginRequest();
        req.setEmail("murali@gmail.com");
        req.setPassword("wrong");

        when(userRepository.findByEmailAndIsActiveTrue(any()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidDataException.class,
                () -> userService.login(req));
    }

    // GET USER BY ID
    @Test
    void getUserById_success() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(modelMapper.map(any(), eq(UserResponse.class)))
                .thenReturn(response);

        UserResponse res = userService.getUserById(1L);

        assertNotNull(res);
    }

    // GET USER BY ID NOT FOUND
    @Test
    void getUserById_notFound() {

        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(1L));
    }

    // DELETE USER
    @Test
    void deleteUser_success() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        String res = userService.deleteUser(1L);

        assertEquals("User deleted successfully", res);
    }

    // GET PROFILE
    @Test
    void getProfile_success() {

        when(userRepository.findByEmailAndIsActiveTrue(any()))
                .thenReturn(Optional.of(user));

        when(modelMapper.map(any(), eq(UserResponse.class)))
                .thenReturn(response);

        UserResponse res = userService.getProfile("murali@gmail.com");

        assertNotNull(res);
    }

    // UPDATE PROFILE
    @Test
    void updateProfile_success() {

        UpdateProfile req = new UpdateProfile();
        req.setName("Murali Updated");

        when(userRepository.findByEmailAndIsActiveTrue(any()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any())).thenReturn(user);

        when(modelMapper.map(any(), eq(UserResponse.class)))
                .thenReturn(response);

        UserResponse res = userService.updateProfile("murali@gmail.com", req);

        assertNotNull(res);
    }

    // DELETE MY ACCOUNT
    @Test
    void deleteMyAccount_success() {

        when(userRepository.findByEmailAndIsActiveTrue(any()))
                .thenReturn(Optional.of(user));

        String res = userService.deleteMyAccount("murali@gmail.com");

        assertEquals("Your account has been permanently deleted", res);
    }

    // RECHARGE HISTORY SERVICE DOWN
    @Test
    void rechargeHistory_serviceDown() {

        when(userRepository.findByEmailAndIsActiveTrue(any()))
                .thenReturn(Optional.of(user));

        when(rechargeClient.getRechargeHistory(anyLong()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> userService.getRechargeHistoryByEmail("murali@gmail.com"));
    }

    // TRANSACTION STATUS SERVICE DOWN
    @Test
    void transactionStatus_serviceDown() {

        when(paymentClient.getTransactionStatus(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> userService.getTransactionStatus("tx123"));
    }

}