package com.home_project.transfer_service.dto;

import com.home_project.transfer_service.validation.ValidBankAccount;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDto {

    @NotNull
    @ValidBankAccount
    private UUID fromAccountId;

    @NotNull
    @ValidBankAccount
    private UUID toAccountId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @Size(max = 500)
    private String description;

    @Future
    private Instant scheduledDate;
}
