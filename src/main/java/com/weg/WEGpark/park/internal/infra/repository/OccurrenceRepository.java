package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OccurrenceRepository extends JpaRepository<Occurrence, Long>, JpaSpecificationExecutor<Occurrence> {

    List<Occurrence> findByDateHourBetween(LocalDateTime start, LocalDateTime end);

    List<Occurrence> findByLocation(String location);
}
