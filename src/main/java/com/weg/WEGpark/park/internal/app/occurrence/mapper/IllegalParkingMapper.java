package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.UpdateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.UpdateVehicleRequestDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IllegalParkingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(source = "request.defaults", target = ".")
    @Mapping(source = "request", target = ".")
    @Mapping(source = "guard", target = "guard")
    IllegalParking toEntity(CreateIllegalParkingRequestDTO request, Guard guard);

    @Mapping(source = "illegalParking", target = ".")
    @Mapping(source = "occurrenceDefaults", target = "defaults")
    CreateIllegalParkingResponseDTO toCreateResponse(IllegalParking illegalParking, DefaultOccurrenceResponseDTO occurrenceDefaults);

    @Mapping(source = ".", target = "defaults")
    GetIllegalParkingResponseDTO toGetResponse(IllegalParking illegalParking);

    @Mapping(source = "defaults", target = ".")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateIllegalParkingRequestDTO dto, @MappingTarget IllegalParking illegalParking);
}
