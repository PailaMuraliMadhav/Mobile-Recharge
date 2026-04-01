package com.example.userservice.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "recharge-service")
public interface RechargeClient {

    @GetMapping("/api/recharges/user/{userId}")
    List<?> getRechargeHistory(@PathVariable("userId") Long userId);
}