package com.weg.WEGpark.park.internal.domain.model.users;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("GUARD")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "guard", schema = "park")
public class Guard extends Collaborator{

    @Column(nullable = false)
    private String boss;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "guard")
    private List<Occurrence> occurrences;

    public Guard(Long id, UUID uuid, String email, String telephone,
                 String name, String badgeNumber, String location, String boss) {
        super(id, uuid, email, telephone, name, badgeNumber, location);
        this.boss = boss;
    }
}
