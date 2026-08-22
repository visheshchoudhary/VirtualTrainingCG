package com.founderlink.startup.client;

import com.founderlink.startup.dto.InvestmentSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

// Calls investment-service via Eureka name lb://INVESTMENT-SERVICE
// If investment-service is down, InvestmentFallback returns empty list
@FeignClient(name = "INVESTMENT-SERVICE", fallback = InvestmentClient.InvestmentFallback.class)
public interface InvestmentClient {

    @GetMapping("/investments/startup/{startupId}")
    List<InvestmentSummary> getInvestmentsByStartup(@PathVariable("startupId") Long startupId);

    @Component
    class InvestmentFallback implements InvestmentClient {

        private static final Logger logger =
                LoggerFactory.getLogger(InvestmentFallback.class);

        @Override
        public List<InvestmentSummary> getInvestmentsByStartup(Long startupId) {
            logger.warn("Circuit breaker OPEN — investment-service unavailable" +
                    " for startupId: {}", startupId);
            return Collections.emptyList();
        }
    }
}
