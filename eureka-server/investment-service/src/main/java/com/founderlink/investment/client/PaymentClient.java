package com.founderlink.investment.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class PaymentClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentClient.class);

    private final WebClient webClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public PaymentClient(WebClient.Builder builder,
                         CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.webClient = builder.baseUrl("http://localhost:8087").build();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public String initiatePayment(Long investmentId, Long senderId,
                                   Long receiverId, Double amount) {

        CircuitBreaker cb = circuitBreakerFactory.create("paymentService");

        return cb.run(
            () -> {
                logger.info("Calling payment-service for investmentId: {}", investmentId);
                Map<String, Object> body = Map.of(
                    "investmentId", investmentId,
                    "senderId",     senderId,
                    "receiverId",   receiverId,
                    "amount",       amount,
                    "paymentMethod","DEFAULT"
                );
                return webClient.post()
                        .uri("/payments")
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            },
            throwable -> {
                logger.warn("payment-service DOWN. Fallback triggered: {}", 
                             throwable.getMessage());
                return "PAYMENT_SERVICE_UNAVAILABLE";
            }
        );
    }
}