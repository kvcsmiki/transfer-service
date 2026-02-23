package com.home_project.transfer_service.event;

import java.util.UUID;

public record TransferFailedEvent(UUID transferId) {}
