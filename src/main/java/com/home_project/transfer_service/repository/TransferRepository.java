package com.home_project.transfer_service.repository;

import com.home_project.transfer_service.entity.Transfer;
import com.home_project.transfer_service.entity.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    List<Transfer> findByFromAccountIdAndCreatedAtBetween(UUID fromAccountId, Instant start, Instant end);

    @Query("SELECT COUNT(t) FROM Transfer t WHERE t.fromAccountId = :userId AND t.createdAt >= :startOfDay")
    long countDailyTransfersByUser(UUID userId, Instant startOfDay);

    @Query("SELECT t FROM Transfer t WHERE t.status = 'PENDING'")
    List<Transfer> findPendingTransfers();

    List<Transfer> findByStatus(TransferStatus status);

    List<Transfer> findByStatusAndCreatedAtBefore(TransferStatus status, Instant date);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transfer t WHERE t.userId = :userId AND t.createdAt >= :startOfDay")
    BigDecimal sumDailyTransfers(UUID userId, Instant startOfDay);

    @Query("SELECT COUNT(t) FROM Transfer t WHERE t.fromAccountId = :from AND t.toAccountId = :to AND t.createdAt >= :since")
    long countRecentTransfers(UUID from, UUID to, Instant since);

    @Query("SELECT SUM(t.amount) FROM Transfer t WHERE t.fromAccountId = :userId OR t.toAccountId = :userId GROUP BY ")
    BigDecimal getTodayTransferSumByUser(@Param("userId") UUID userId);

    Optional<Transfer> findByIdAndFromAccountId(UUID id, UUID fromAccountId);
}
