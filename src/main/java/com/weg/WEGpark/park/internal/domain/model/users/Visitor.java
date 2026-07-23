package com.weg.WEGpark.park.internal.domain.model.users;

import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("VISITOR")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "visitor", schema = "park")
public class Visitor extends ParkUser {

    private String company;

    @Column(nullable = false)
    private String cpf;

    public Visitor(Long id, UUID uuid, String email, String telephone,
                   String name, String company, String cpf) {
        super(id, uuid, email, telephone, name);
        this.company = company;
        this.cpf = cpf;
    }
}
