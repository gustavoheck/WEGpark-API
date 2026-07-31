package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.UpdateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.GetTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.UpdateTrafficAccidentRequestDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TrafficAccidentMapper {

        @Mapping(source = "request.defaults", target = ".")
        @Mapping(source = "request", target = ".")
        @Mapping(source = "registerInfo", target = ".")
        TrafficAccident toEntity(CreateTrafficAccidentRequestDTO request, RegisterDefaultInfo registerInfo);

        @Mapping(source = ".", target = "defaults")
        CreateTrafficAccidentResponseDTO toCreateResponse(TrafficAccident trafficAccident);

        @Mapping(source = ".", target = "defaults")
        GetTrafficAccidentResponseDTO toGetResponse(TrafficAccident trafficAccident);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(source = "defaults", target = ".")
        void updateFromDto(UpdateTrafficAccidentRequestDTO dto, @MappingTarget TrafficAccident trafficAccident);
}
