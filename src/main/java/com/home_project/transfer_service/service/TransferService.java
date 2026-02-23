package com.home_project.transfer_service.service;

import com.home_project.transfer_service.client.AccountServiceClient;
import com.home_project.transfer_service.dto.TransferRequestDto;
import com.home_project.transfer_service.dto.TransferResponseDto;
import com.home_project.transfer_service.entity.Transfer;
import com.home_project.transfer_service.event.TransferCompletedEvent;
import com.home_project.transfer_service.event.TransferFailedEvent;
import com.home_project.transfer_service.exception.*;
import com.home_project.transfer_service.mapper.TransferMapper;
import com.home_project.transfer_service.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private static final BigDecimal DAILY_LIMIT = BigDecimal.valueOf(2_000_000);

    private final TransferRepository transferRepository;
    private final AccountServiceClient accountClient;
    private final ApplicationEventPublisher eventPublisher;
    private final TransferMapper transferMapper;

    @Transactional(readOnly = true)
    public TransferResponseDto getById(UUID id) {
        UUID userId = currentUserId();

        Transfer transfer = transferRepository
                .findByIdAndFromAccountId(id, userId)
                .orElseThrow(TransferNotFoundException::new);

        return transferMapper.toTransferResponse(transfer);
    }

    @Transactional(readOnly = true)
    public List<TransferResponseDto> getByUserId(UUID userId) {
        UUID currentUser = currentUserId();

        if (!currentUser.equals(userId)) {
            log.error(String.format("User with id %s tried to access another user's transfer", currentUser));
            throw new AccessDeniedException("Cannot access other user's transfers");
        }

        return transferMapper.toTransferResponseList(
                transferRepository.findByFromAccountIdAndCreatedAtBetween(userId, Instant.MIN, Instant.now())
        );
    }

    public TransferResponseDto create(TransferRequestDto request) {
        Transfer transfer = transferMapper.toTransfer(request);
        transferRepository.save(transfer);

        validateTransfer(transfer);
        executeTransfer(transfer);

        return transferMapper.toTransferResponse(transfer);
    }

    public TransferResponseDto cancel(UUID id) {
        UUID userId = currentUserId();
        Transfer transfer = transferRepository
                .findByIdAndFromAccountId(id, userId)
                .orElseThrow(TransferNotFoundException::new);
        transfer.cancel();
        transferRepository.save(transfer);

        log.info(String.format("Transfer with id %s cancelled successfully", id));

        return transferMapper.toTransferResponse(transfer);
    }

    private void validateTransfer(Transfer transfer) {
        if (!accountClient.validateAccounts(transfer.getFromAccountId(), transfer.getToAccountId())) {
            log.error(String.format("One or both the provided accounts are not valid: (%s, %s)", transfer.getFromAccountId(), transfer.getToAccountId()));
            throw new AccountNotFoundException(); // AccountClient should throw these exceptions, but it would not be testable..
        }
        checkDailyLimit(transfer);
        fraudDetection(transfer);
        if (!accountClient.checkBalance(transfer.getFromAccountId(), transfer.getAmount())) {
            log.error(String.format("Transfer amount (%s) exceeds account balance", transfer.getAmount().toString()));
            throw new InsufficientFundsException(); // AccountClient should throw these exceptions, but it would not be testable..
        }
        log.info(String.format("Transfer with id %s is valid", transfer.getId()));
    }

    private void checkDailyLimit(Transfer transfer) {
        BigDecimal dailyTotal =
                transferRepository.sumDailyTransfers(
                        transfer.getFromAccountId(),
                        LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
                );

        if (dailyTotal.add(transfer.getAmount()).compareTo(DAILY_LIMIT) > 0) {
            log.error(String.format(
                    "Daily transfer amounts (%s) with current amount (%s) exceeds daily limit (%s)",
                    dailyTotal,
                    transfer.getAmount().toString(),
                    DAILY_LIMIT
            ));
            throw new DailyLimitExceededException();
        }
    }

    private void fraudDetection(Transfer transfer) {
        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        long count = transferRepository.countRecentTransfers(
                transfer.getFromAccountId(),
                transfer.getToAccountId(),
                fiveMinutesAgo
        );
        if (count >= 3) {
            log.error(String.format("Fraud detected, recent transfer count: %d", count));
            throw new FraudDetectionException();
        }
    }

    private void executeTransfer(Transfer transfer) {
        try {
            accountClient.debit(
                    transfer.getFromAccountId(),
                    transfer.getAmount()
            );
            accountClient.credit(
                    transfer.getToAccountId(),
                    transfer.getAmount()
            );

            transfer.complete();
            transferRepository.save(transfer);
            log.info(String.format("Transfer with id %s completed successfully", transfer.getId()));

            eventPublisher.publishEvent(
                    new TransferCompletedEvent(transfer.getId())
            );

        } catch (Exception ex) {
            accountClient.rollbackDebit(
                    transfer.getFromAccountId(),
                    transfer.getAmount()
            );
            transfer.fail();
            transferRepository.save(transfer);
            log.error(String.format("Executing transfer with id %s has failed", transfer.getId()));
            eventPublisher.publishEvent(
                    new TransferFailedEvent(transfer.getId())
            );
            throw ex;
        }
    }

    private UUID currentUserId() {
        return UUID.fromString(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );
    }
}
