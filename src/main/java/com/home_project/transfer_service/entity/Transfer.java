package com.home_project.transfer_service.entity;

import com.home_project.transfer_service.exception.TransferNotCancelableException;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Transfer {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID fromAccountId;

    @Column(nullable = false)
    private UUID toAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant scheduledDate;

    private Instant executedDate;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = TransferStatus.PENDING;
    }

    public void markMoneyMoved() {
        this.status = TransferStatus.MONEY_MOVED;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        this.status = TransferStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public void fail() {
        this.status = TransferStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (this.status != TransferStatus.PENDING) {
            log.error(String.format("Transfer with id %s can't be cancelled, because it's status (%s) is not pending", this.id, this.status));
            throw new TransferNotCancelableException();
        }
        this.status = TransferStatus.FAILED;
        this.updatedAt = Instant.now();
    }
}
