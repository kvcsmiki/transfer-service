package com.home_project.transfer_service.service;

import com.home_project.transfer_service.client.AccountServiceClient;
import com.home_project.transfer_service.dto.TransferRequestDto;
import com.home_project.transfer_service.dto.TransferResponseDto;
import com.home_project.transfer_service.entity.Transfer;
import com.home_project.transfer_service.exception.AccountNotFoundException;
import com.home_project.transfer_service.exception.DailyLimitExceededException;
import com.home_project.transfer_service.exception.InsufficientFundsException;
import com.home_project.transfer_service.mapper.TransferMapper;
import com.home_project.transfer_service.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private TransferMapper transferMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private TransferService transferService;

    @Test
    void shouldCreateTransferSuccessfully() {
        TransferRequestDto request = validRequest();
        Transfer saved = validTransfer();

        when(transferMapper.toTransfer(request))
                .thenReturn(saved);
        when(accountServiceClient.validateAccounts(any(), any()))
                .thenReturn(true);
        when(transferRepository.sumDailyTransfers(any(), any()))
                .thenReturn(BigDecimal.valueOf(0));
        when(accountServiceClient.checkBalance(any(), eq(BigDecimal.valueOf(500_000))))
                .thenReturn(true);

        when(transferMapper.toTransferResponse(any()))
                .thenReturn(new TransferResponseDto());

        TransferResponseDto response = transferService.create(request);

        assertNotNull(response);

        verify(accountServiceClient).validateAccounts(saved.getFromAccountId(), saved.getToAccountId());
    }

    @Test
    void shouldRejectTransferWhenDailyLimitExceeded() {
        TransferRequestDto request = validRequest();
        Transfer saved = validTransfer();

        when(transferMapper.toTransfer(request))
                .thenReturn(saved);
        when(accountServiceClient.validateAccounts(any(), any())).thenReturn(true);

        when(transferRepository.sumDailyTransfers(any(), any()))
                .thenReturn(BigDecimal.valueOf(1_600_000));

        assertThrows(DailyLimitExceededException.class,
                () -> transferService.create(request));
    }

    @Test
    void shouldRejectTransferWhenAccountsAreNotValid() {
        TransferRequestDto request = validRequest();
        Transfer saved = validTransfer();

        when(transferMapper.toTransfer(request))
                .thenReturn(saved);

        when(accountServiceClient.validateAccounts(any(), any())).thenReturn(false);

        assertThrows(AccountNotFoundException.class,
                () -> transferService.create(request));
    }

    @Test
    void shouldRejectTransferWhenAmountExceedsBalance() {
        TransferRequestDto request = validRequest();
        Transfer saved = validTransfer();

        when(transferMapper.toTransfer(request))
                .thenReturn(saved);
        when(accountServiceClient.validateAccounts(any(), any())).thenReturn(true);
        when(transferRepository.sumDailyTransfers(any(),any())).thenReturn(BigDecimal.valueOf(0));

        when(accountServiceClient.checkBalance(any(), eq(BigDecimal.valueOf(500_000)))).thenReturn(false);

        assertThrows(InsufficientFundsException.class,
                () -> transferService.create(request));
    }

    private TransferRequestDto validRequest() {
        return TransferRequestDto.builder()
                .amount(BigDecimal.valueOf(500_000))
                .fromAccountId(UUID.randomUUID())
                .toAccountId(UUID.randomUUID())
                .currency("HUF")
                .build();
    }

    private Transfer validTransfer() {
        return Transfer.builder()
                .amount(BigDecimal.valueOf(500_000))
                .fromAccountId(UUID.randomUUID())
                .toAccountId(UUID.randomUUID())
                .currency("HUF")
                .build();
    }
}
