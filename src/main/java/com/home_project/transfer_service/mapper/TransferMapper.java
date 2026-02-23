package com.home_project.transfer_service.mapper;

import com.home_project.transfer_service.dto.TransferRequestDto;
import com.home_project.transfer_service.dto.TransferResponseDto;
import com.home_project.transfer_service.entity.Transfer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    TransferResponseDto toTransferResponse(Transfer entity);

    List<TransferResponseDto> toTransferResponseList(List<Transfer> entities);

    TransferRequestDto toTransferRequest(Transfer entity);

    Transfer toTransfer(TransferRequestDto dto);
}
