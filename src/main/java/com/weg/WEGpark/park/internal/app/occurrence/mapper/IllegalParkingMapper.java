package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IllegalParkingMapper {

    @Mapping(source = "defaults", target = ".")
    IllegalParking toEntity(CreateIllegalParkingRequestDTO request);

    @Mapping(source = ".", target = "defaults")
    CreateIllegalParkingResponseDTO toCreateResponse(IllegalParking illegalParking);

    GetIllegalParkingResponseDTO toGetResponse(IllegalParking illegalParking);
}
