package com.home_project.transfer_service.client;

import com.home_project.transfer_service.exception.TechnicalException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountServiceClient {

    private final WebClient webClient;

    @CircuitBreaker(name = "accountService")
    @Retry(name = "accountService")
    public boolean validateAccounts(UUID from, UUID to) { // return type is boolean to make it testable
        call("/accounts/validate", Map.of(
                "from", from,
                "to", to
        ));
        return true;
    }

    @CircuitBreaker(name = "accountService")
    @Retry(name = "accountService")
    public boolean checkBalance(UUID accountId, BigDecimal amount) { // return type is boolean to make it testable
        call("/accounts/balance/check", Map.of(
                "accountId", accountId,
                "amount", amount
        ));
        return true;
    }

    @CircuitBreaker(name = "accountService")
    @Retry(name = "accountService")
    public void debit(UUID accountId, BigDecimal amount) {
        call("/accounts/debit", Map.of(
                "accountId", accountId,
                "amount", amount
        ));
    }

    @CircuitBreaker(name = "accountService")
    @Retry(name = "accountService")
    public void credit(UUID accountId, BigDecimal amount) {
        call("/accounts/credit", Map.of(
                "accountId", accountId,
                "amount", amount
        ));
    }

    public void rollbackDebit(UUID accountId, BigDecimal amount) {
        call("/accounts/rollback", Map.of(
                "accountId", accountId,
                "amount", amount
        ));
    }

    private void call(String path, Object body) {
        webClient.post()
                .uri(path)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        r -> Mono.error(new TechnicalException())
                )
                .toBodilessEntity()
                .block();
    }
}

