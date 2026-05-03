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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: UserService
 * DESCRIPTION:
 *   Business logic layer for user account management.
 *   Handles registration, authentication, profile operations, soft/hard deletes,
 *   and cross-service calls to retrieve recharge history and transaction status.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RechargeClient rechargeServiceClient;
    private final PaymentClient paymentServiceClient;

    private static final String USER_NOT_FOUND_ID = "User not found with id: ";
    private static final String USER_NOT_FOUND_EMAIL = "User not found: ";

    /*
     * ================================================================
     * METHOD: register
     * DESCRIPTION:
     * Registers a new user after validating that the email and phone number
     * are not already in use. Encodes the password before persisting the record.
     * ================================================================
     */
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
        user.setRole(Role.USER); // Role is always USER; admins are created directly via SQL

        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponse.class);
    }

    /*
     * ================================================================
     * METHOD: login
     * DESCRIPTION:
     * Authenticates a user by verifying their email and password, then generates
     * and returns a JWT token along with user details on success.
     * ================================================================
     */
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(
                        "No account found with email: " + request.getEmail()));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new InvalidDataException("Your account has been blocked. Please contact us at recharge.omni@gmail.com for assistance.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidDataException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name());

        return new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole(),
                jwtUtil.getExpiration());
    }

    /*
     * ================================================================
     * METHOD: getAllUsers
     * DESCRIPTION:
     * Returns a list of all registered users, intended for administrative use.
     * ================================================================
     */
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();
    }

    /*
     * ================================================================
     * METHOD: getUserById
     * DESCRIPTION:
     * Retrieves a single user by their ID. Throws NotFoundException if not found.
     * ================================================================
     */
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_ID + id));

        return modelMapper.map(user, UserResponse.class);
    }

    /*
     * ================================================================
     * METHOD: blockUser
     * DESCRIPTION:
     * Blocks a user by setting their active flag to false, preventing login.
     * The account record is preserved in the database.
     * ================================================================
     */
    public String blockUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_ID + id));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new InvalidDataException("User is already blocked.");
        }

        user.setIsActive(false);
        userRepository.save(user);
        return "User blocked successfully";
    }

    /*
     * ================================================================
     * METHOD: unblockUser
     * DESCRIPTION:
     * Unblocks a previously blocked user by setting their active flag to true,
     * restoring their ability to log in.
     * ================================================================
     */
    public String unblockUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_ID + id));

        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new InvalidDataException("User is already active.");
        }

        user.setIsActive(true);
        userRepository.save(user);
        return "User unblocked successfully";
    }

    /*
     * ================================================================
     * METHOD: deleteUser
     * DESCRIPTION:
     * Soft-deletes a user by setting their active flag to false,
     * preserving the account record in the database.
     * ================================================================
     */
    public String deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_ID + id));

        user.setIsActive(false);
        userRepository.save(user);
        return "User deleted successfully";
    }

    /*
     * ================================================================
     * METHOD: getProfile
     * DESCRIPTION:
     * Retrieves the profile of the currently authenticated user identified by
     * email.
     * ================================================================
     */
    public UserResponse getProfile(String email) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_EMAIL + email));

        return modelMapper.map(user, UserResponse.class);
    }

    /*
     * ================================================================
     * METHOD: updateProfile
     * DESCRIPTION:
     * Updates the name and/or phone number of the authenticated user's profile,
     * validating that the new phone number is not already taken by another account.
     * ================================================================
     */
    public UserResponse updateProfile(String email, UpdateProfile request) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_EMAIL + email));

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

    /*
     * ================================================================
     * METHOD: deleteMyAccount
     * DESCRIPTION:
     * Permanently deletes the authenticated user's account from the database.
     * ================================================================
     */
    public String deleteMyAccount(String email) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_EMAIL + email));

        userRepository.deleteById(user.getId());
        return "Your account has been permanently deleted";
    }

    /*
     * ================================================================
     * METHOD: permanentDeleteUser
     * DESCRIPTION:
     * Permanently removes a user record from the database by their ID,
     * intended for administrative hard-delete operations.
     * ================================================================
     */
    public String permanentDeleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_ID + id));

        userRepository.deleteById(user.getId());
        return "User with id " + id + " has been permanently deleted";
    }

    /*
     * ================================================================
     * METHOD: getRechargeHistoryByEmail
     * DESCRIPTION:
     * Resolves the user ID from the given email, then fetches the recharge history
     * via a Feign client call to the Recharge Service.
     * ================================================================
     */
    @CircuitBreaker(name = "rechargeService", fallbackMethod = "rechargeHistoryFallback")
    public List<Object> getRechargeHistoryByEmail(String email) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_EMAIL + email));

        try {
            return rechargeServiceClient.getRechargeHistory(user.getId());
        } catch (Exception e) {
            throw new InvalidDataException("Recharge service unavailable. Please try again later.");
        }
    }

    public List<Object> rechargeHistoryFallback(String email, Throwable t) {
        log.error("Circuit Breaker 'rechargeService' is OPEN or call failed for email: {}. Reason: {}", email, t.getMessage());
        return java.util.Collections.emptyList();
    }

    /*
     * ================================================================
     * METHOD: getTransactionStatus
     * DESCRIPTION:
     * Fetches the status of a payment transaction by its ID via a Feign client
     * call to the Payment Service.
     * ================================================================
     */
    @CircuitBreaker(name = "paymentService", fallbackMethod = "transactionStatusFallback")
    public Object getTransactionStatus(String transactionId) {

        try {
            return paymentServiceClient.getTransactionStatus(transactionId);
        } catch (Exception e) {
            throw new InvalidDataException("Payment service unavailable. Please try again later.");
        }
    }

    public Object transactionStatusFallback(String transactionId, Throwable t) {
        log.error("Circuit Breaker 'paymentService' is OPEN or call failed for txnId: {}. Reason: {}", transactionId, t.getMessage());
        return "Transaction status unavailable (Circuit Breaker active)";
    }

}
