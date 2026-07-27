package com.weg.WEGpark.rh.internal.domain.model;

import com.weg.WEGpark.rh.internal.domain.embeddable.HistoricId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "rh",name = "historic")
public class Historic {

    @EmbeddedId
    private HistoricId id;

    @Column(name = "date_hour", nullable = false)
    private LocalDateTime dateHour;

    public Historic(HistoricId id) {
        this.id = id;
        this.dateHour = LocalDateTime.now();
    }
}
