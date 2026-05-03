package com.example.userservice.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeClient
 * DESCRIPTION:
 *   Feign client interface for inter-service HTTP communication with the Recharge Service.
 *   Used by UserService to fetch a user's recharge history for the recharge-history endpoint.
 */
@FeignClient(name = "recharge-service")
public interface RechargeClient {

    /* ================================================================
     * METHOD: getRechargeHistory
     * DESCRIPTION:
     *   Retrieves the complete recharge history for a user identified by their user ID.
     * ================================================================ */
    @GetMapping("/api/recharges/user/{userId}")
    List<Object> getRechargeHistory(@PathVariable("userId") Long userId);
}
