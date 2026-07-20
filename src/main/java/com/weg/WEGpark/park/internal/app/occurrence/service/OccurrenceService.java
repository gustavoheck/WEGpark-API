package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceMapper;
import com.weg.WEGpark.park.internal.app.shared.util.FilterUtil;
import com.weg.WEGpark.park.internal.app.vehicle.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceResponseDto;
import com.weg.WEGpark.park.internal.dto.occurrence.filter.FilterOccurrenceRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import com.weg.WEGpark.park.internal.infra.specification.OccurrenceSpecification;
import com.weg.WEGpark.park.internal.infra.specification.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final OccurrenceMapper occurrenceMapper;

    public List<OccurrenceResponseDto> findAllOccurrences(FilterOccurrenceRequestDTO filter) {

        if (FilterUtil.checkMoreThanOneFilter(filter)) {
            Specification<Occurrence> spec = Specification.where(OccurrenceSpecification.hasLocal(filter.location()));
            spec = spec.and(OccurrenceSpecification.hasGate(filter.location()))
                    .and(OccurrenceSpecification.hasDate(filter.yearMonth()))
                    .and(OccurrenceSpecification.hasType(filter.occurrenceType().toString()))
                    .and(OccurrenceSpecification.hasRecents(filter.recents()));

            List<Occurrence> occurrenceList = occurrenceRepository.findAll(spec);

            return occurrenceList
                    .stream()
                    .map(occurrenceMapper::toResponse)
                    .toList();
        }
        throw new MoreThenOneFilterException("You can't use more than one filter");
    }
}
