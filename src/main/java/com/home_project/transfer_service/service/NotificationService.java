package com.home_project.transfer_service.service;

import com.home_project.transfer_service.entity.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    @Async
    public void sendTransferNotification(Transfer transfer) {
        // email / push / kafka / webhook
        log.info("Notification sent for transfer {}", transfer.getId());
    }
}
