package com.home_project.transfer_service.service;

import com.home_project.transfer_service.client.AccountServiceClient;
import com.home_project.transfer_service.entity.Transfer;
import com.home_project.transfer_service.event.TransferCompletedEvent;
import com.home_project.transfer_service.event.TransferFailedEvent;
import com.home_project.transfer_service.repository.TransferRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class TransferSaga {

    private final AccountServiceClient accountClient;
    private final TransferRepository transferRepository;
    private final ApplicationEventPublisher events;

    public TransferSaga(AccountServiceClient accountClient, TransferRepository transferRepository, ApplicationEventPublisher events) {
        this.accountClient = accountClient;
        this.transferRepository = transferRepository;
        this.events = events;
    }

    public void execute(Transfer transfer) {
        try {
            accountClient.debit(transfer.getFromAccountId(), transfer.getAmount());
            accountClient.credit(transfer.getToAccountId(), transfer.getAmount());

            transfer.markMoneyMoved();
            transferRepository.save(transfer);

            transfer.complete();
            transferRepository.save(transfer);

            events.publishEvent(new TransferCompletedEvent(transfer.getId()));

        } catch (Exception e) {
            accountClient.rollbackDebit(transfer.getFromAccountId(), transfer.getAmount());
            transfer.fail();
            transferRepository.save(transfer);

            events.publishEvent(new TransferFailedEvent(transfer.getId()));
            throw e;
        }
    }
}
