package com.example.rechargeservice.feignclients;

import com.example.rechargeservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: UserClient
 * DESCRIPTION:
 *   Feign client interface for inter-service HTTP communication with the User Service.
 *   Used by RechargeService to fetch user details (email) for notification purposes.
 */
@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    /* ================================================================
     * METHOD: getUserById
     * DESCRIPTION:
     *   Retrieves user details by user ID via the internal endpoint of the User Service.
     * ================================================================ */
    @GetMapping("/api/users/internal/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}
