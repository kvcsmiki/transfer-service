package com.home_project.transfer_service.event;

import com.home_project.transfer_service.entity.Transfer;
import com.home_project.transfer_service.repository.TransferRepository;
import com.home_project.transfer_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferEventListener {

    private final NotificationService notificationService;
    private final TransferRepository repository;

    @EventListener
    public void onCompleted(TransferCompletedEvent event) {
        Transfer transfer = repository.findById(event.transferId())
                .orElseThrow();
        notificationService.sendTransferNotification(transfer);
    }
}
