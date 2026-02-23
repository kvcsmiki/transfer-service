package com.home_project.transfer_service.dto;

import com.home_project.transfer_service.entity.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDto {
    private UUID id;
    private TransferStatus status;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
