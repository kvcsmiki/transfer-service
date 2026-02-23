package com.home_project.transfer_service.controller;

import com.home_project.transfer_service.dto.TransferRequestDto;
import com.home_project.transfer_service.dto.TransferResponseDto;
import com.home_project.transfer_service.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@Validated
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransferResponseDto> createTransfer(
            @Valid @RequestBody TransferRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transferService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public TransferResponseDto getById(@PathVariable UUID id) {
        return transferService.getById(id);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') and #userId == authentication.name")
    public List<TransferResponseDto> getByUser(@PathVariable UUID userId) {
        return transferService.getByUserId(userId);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public TransferResponseDto cancel(@PathVariable UUID id) {
        return transferService.cancel(id);
    }
}

