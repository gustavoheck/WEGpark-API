package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.IllegalParkingMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.TrafficAccidentMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.WarningMapper;
import com.weg.WEGpark.park.internal.app.shared.util.FilterUtil;
import com.weg.WEGpark.park.internal.app.vehicle.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.GetOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.filter.FilterOccurrenceRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.GetTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import com.weg.WEGpark.park.internal.infra.specification.OccurrenceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;

    private final IllegalParkingMapper illegalParkingMapper;
    private final TrafficAccidentMapper trafficAccidentMapper;
    private final WarningMapper warningMapper;

    public GetOccurrenceResponseDTO findAllOccurrences(FilterOccurrenceRequestDTO filter) {

        if (FilterUtil.checkMoreThanOneFilter(filter)) {
            Specification<Occurrence> spec = Specification
                    .where(OccurrenceSpecification.hasLocal(filter.location()))
                    .and(OccurrenceSpecification.hasGate(filter.gate()))
                    .and(OccurrenceSpecification.hasDate(filter.yearMonth()))
                    .and(OccurrenceSpecification.hasType(filter.occurrenceType() == null ? null : filter.occurrenceType().toString()))
                    .and(OccurrenceSpecification.hasRecents(filter.recents()));

            List<Occurrence> occurrenceList = occurrenceRepository.findAll(spec);

            List<GetTrafficAccidentResponseDTO> responseTrafficAccidentList = new ArrayList<>();
            List<GetWarningResponseDTO> responseWarningList = new ArrayList<>();
            List<GetIllegalParkingResponseDTO> responseIllegalParkingList = new ArrayList<>();

            for(Occurrence occurrence : occurrenceList) {
                switch (occurrence) {
                    case Warning warning -> {
                        responseWarningList.add(warningMapper.toGetResponse(warning));
                    }
                    case IllegalParking illegalParking -> {
                        responseIllegalParkingList.add(illegalParkingMapper.toGetResponse(illegalParking));
                    }
                    case TrafficAccident trafficAccident -> {
                        responseTrafficAccidentList.add(trafficAccidentMapper.toGetResponse(trafficAccident));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + occurrence);
                }
            }
            return new GetOccurrenceResponseDTO(responseIllegalParkingList, responseTrafficAccidentList, responseWarningList);
        }
        throw new MoreThenOneFilterException("You can't use more than one filter");
    }
}
