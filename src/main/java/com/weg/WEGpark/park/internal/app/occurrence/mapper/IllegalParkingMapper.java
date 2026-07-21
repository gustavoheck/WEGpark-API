package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.UpdateVehicleRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IllegalParkingMapper {

    @Mapping(source = "defaults", target = ".")
    IllegalParking toEntity(CreateIllegalParkingRequestDTO request);

    @Mapping(source = ".", target = "defaults")
    CreateIllegalParkingResponseDTO toCreateResponse(IllegalParking illegalParking);

    @Mapping(source = ".", target = "defaults")
    GetIllegalParkingResponseDTO toGetResponse(IllegalParking illegalParking);

    void updateIllegalParkingFromDto(UpdateVehicleRequestDTO dto, @MappingTarget Vehicle vehicle);
}
