package com.weg.WEGpark.park.internal.infra.specification;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public class OccurrenceSpecification {

    public static Specification<Occurrence> hasLocal(String location) {
        return (root, query, cb) -> {
           if (location == null || location.trim().isEmpty()) {
               return null;
           }
           return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<Occurrence> hasGate(String gate) {
        return (root, query, cb) -> {
            if (gate == null || gate.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("gate")), "%" + gate.toLowerCase() + "%");
        };
    }

    public static Specification<Occurrence> hasType(String type) {
        return (root, query, cb) -> {
            if (type == null || type.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("occurrenceType")), "%" + type.toLowerCase() + "%");
        };
    }

    public static Specification<Occurrence> hasDate(YearMonth yearMonth) {
        return (root, query, cb) -> {
            if (yearMonth == null) {
                return null;
            }

            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

            return cb.between(root.get("dateHour"), startOfMonth, endOfMonth);
        };
    }

    public static Specification<Occurrence> hasRecents (Boolean searchRecents) {
        return (root, query, cb) -> {
            if (searchRecents == null || !searchRecents) {
                return null;
            }

            LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = YearMonth.now().atEndOfMonth().atTime(LocalTime.MAX);

            return cb.between(root.get("dateHour"), startOfMonth, endOfMonth);
        };
    }

    public static Specification<Occurrence> hasPlate (String plate) {
        return (root, query, cb) -> {
            if (plate == null || plate.trim().isEmpty()) {
                return null;
            }
            Join<Occurrence, VehicleUser> vehicleUser = root.join("vehicleUsers");
            Join<VehicleUser, Vehicle> vehicle = vehicleUser.join("vehicle");

            return cb.equal(cb.lower(vehicle.get("plate")), plate.toLowerCase());
        };
    }

    public static Specification<Occurrence> hasResponsibleName (String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return null;
            }
            Join<Occurrence, VehicleUser> vehicleUser = root.join("vehicleUsers");
            Join<VehicleUser, ParkUser> parkUser = vehicleUser.join("parkUser");

            return cb.equal(cb.lower(parkUser.get("name")), name.toLowerCase());
        };
    }

    public static Specification<Occurrence> hasBadgeNumber (String badgeNumber) {
        return (root, query, cb) -> {
            if (badgeNumber == null || badgeNumber.trim().isEmpty()) {
                return null;
            }
            Join<Occurrence, VehicleUser> vehicleUser = root.join("vehicleUsers");
            Join<VehicleUser, Collaborator> collaborator = vehicleUser.join("parkUser");

            return cb.equal(cb.lower(collaborator.get("badgeNumber")), badgeNumber);
        };
    }
}
