package com.weg.WEGpark.rh.internal.domain.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HistoricId implements Serializable {

    @Column(name = "id_rh")
    private Long idRh;

    @Column(name = "id_operation")
    private Long idOperation;
}