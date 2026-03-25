package com.example.User_Service.Service;

import com.example.User_Service.Dto.*;
import com.example.User_Service.Entity.User;
import com.example.User_Service.Enums.Role;
import com.example.User_Service.Exceptions.DuplicateException;
import com.example.User_Service.Exceptions.InvalidDataException;
import com.example.User_Service.Exceptions.NotFoundException;
import com.example.User_Service.FeignClients.PaymentClient;
import com.example.User_Service.FeignClients.RechargeClient;
import com.example.User_Service.Repository.UserRepository;
import com.example.User_Service.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ModelMapper           modelMapper;
    private final JwtUtil               jwtUtil;
    private final UserRepository        userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RechargeClient        rechargeServiceClient;
    private final PaymentClient         paymentServiceClient;

    //  Register
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateException(
                    "Email already registered: " + request.getEmail());
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateException(
                    "Phone number already registered: " + request.getPhoneNumber());
        }

        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponse.class);
    }

    // Login
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new NotFoundException(
                        "No account found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidDataException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name()
        );

        return new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole(),
                jwtUtil.getExpiration()
        );
    }
    //     Get All Users
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    //get user by id
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User not found with id: " + id));

        return modelMapper.map(user, UserResponse.class);
    }

    // delete but not permanant
    public String deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User not found with id: " + id));

        user.setIsActive(false);
        userRepository.save(user);
        return "User deleted successfully";
    }

    //Get Profile
    public UserResponse getProfile(String email) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new NotFoundException(
                        "User not found: " + email));

        return modelMapper.map(user, UserResponse.class);
    }

    //  Update
    public UserResponse updateProfile(String email, UpdateProfile request) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new NotFoundException(
                        "User not found: " + email));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (userRepository.existsByPhoneNumberAndIdNot(
                    request.getPhoneNumber(), user.getId())) {
                throw new DuplicateException(
                        "Phone number already in use: " + request.getPhoneNumber());
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        User updated = userRepository.save(user);
        return modelMapper.map(updated, UserResponse.class);
    }

    // 5. Delete permanent
    public String deleteMyAccount(String email) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new NotFoundException(
                        "User not found: " + email));

        userRepository.deleteById(user.getId());
        return "Your account has been permanently deleted";
    }

    //Recharge History — email from JWT → find userId → Feign call

    public List<?> getRechargeHistoryByEmail(String email) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new NotFoundException(
                        "User not found: " + email));

        try {
            return rechargeServiceClient.getRechargeHistory(user.getId());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Recharge service unavailable. Please try again later.");
        }
    }

//    Transaction Status — Feign

    public Object getTransactionStatus(String transactionId) {

        try {
            return paymentServiceClient.getTransactionStatus(transactionId);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Payment service unavailable. Please try again later.");
        }
    }


}