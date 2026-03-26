package com.example.Api_Gateway.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/fallback/recharge")
    public Mono<String> rechargeFallback(){
        return Mono.just("Recharge service unavailable");
    }

    @GetMapping("/fallback/payment")
    public Mono<String> paymentFallback(){
        return Mono.just("Payment service unavailable");
    }
    @GetMapping("/fallback/operator")
    public Mono<String> operatorFallback(){
        return Mono.just("Operator service unavailable");
    }
}
