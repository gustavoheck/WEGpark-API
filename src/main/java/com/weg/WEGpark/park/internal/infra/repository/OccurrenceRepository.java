package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OccurrenceRepository extends JpaRepository<Occurrence, Long>, JpaSpecificationExecutor<Occurrence> {

    Optional<Occurrence> findByUuid (UUID uuid);

    @Query(value = """
        SELECT COUNT(DISTINCT o)
        FROM Occurrence o
        JOIN o.vehicleUsers vu
        WHERE vu.parkUser.id = :id
          AND o.dateHour > :date
        """)
    Integer countHowManyOccurrencesLastDays(@Param(value = "id") Long id, @Param(value = "date") LocalDateTime date);
}
