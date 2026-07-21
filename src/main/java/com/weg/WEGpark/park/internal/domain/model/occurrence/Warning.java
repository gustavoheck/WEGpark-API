package com.weg.WEGpark.park.internal.domain.model.occurrence;


import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("WARNING")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "warning", schema = "park")
public class Warning extends Occurrence {

    @Enumerated(EnumType.STRING)
    @Column(name = "warning_type", nullable = false)
    private WarningType warningType;

    private String description;

    public Warning(String location, String gate, OccurrenceType occurrenceType, WarningType warningType, String description) {
        super(location, gate, occurrenceType);
        this.warningType = warningType;
        this.description = description;
    }
}
