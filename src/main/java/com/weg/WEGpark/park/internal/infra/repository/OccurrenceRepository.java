package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.Occurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {

}
