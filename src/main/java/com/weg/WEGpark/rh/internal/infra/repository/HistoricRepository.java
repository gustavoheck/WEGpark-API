package com.weg.WEGpark.rh.internal.infra.repository;

import com.weg.WEGpark.rh.internal.domain.embeddable.HistoricId;
import com.weg.WEGpark.rh.internal.domain.model.Historic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricRepository extends JpaRepository<Historic, HistoricId> {
}
