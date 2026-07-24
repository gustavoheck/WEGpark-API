package com.weg.WEGpark.notification.internal.domain.entities;

import com.weg.WEGpark.notification.internal.domain.enums.NotificationType;
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
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("VEHICLE_ASSOCIATION")
@Table(schema = "notification",name = "vehicle_association_notification")
public class VehicleAssociationNotification extends Notification {

    @Column(name = "id_vehicle")
    private Long idVehicleToAssociate;

    @Column(name = "id_user_to_associate")
    private Long idUserToAssociate;

    public VehicleAssociationNotification(Long idNotificatedUser, Long idVehicleToAssociate, Long idUserToAssociate) {
        super(idNotificatedUser, NotificationType.VEHICLE_ASSOCIATION);
        this.idVehicleToAssociate = idVehicleToAssociate;
        this.idUserToAssociate = idUserToAssociate;
    }
}
