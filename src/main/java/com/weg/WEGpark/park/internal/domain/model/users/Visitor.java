package com.weg.WEGpark.park.internal.domain.model.users;

import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
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
@DiscriminatorValue("COLLABORATOR")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "visitor", schema = "park")
public class Visitor extends ParkUser {

    private String company;

    @Column(nullable = false)
    private String cpf;

    public Visitor(String email, String telephone, String name, ParkUserType userType, String company, String cpf) {
        super(email, telephone, name, userType);
        this.company = company;
        this.cpf = cpf;
    }
}
