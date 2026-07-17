package com.weg.WEGpark.park.internal.infra.specification;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecification {

    public static Specification<Vehicle> hasPlate(String plate) {
        return (root, query, cb) -> {
            if (plate == null || plate.trim().isEmpty()) {
                return null;
            }
            return cb.equal(root.get("plate"), plate);
        };
    }

    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null || model.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%");
        };
    }

    public static Specification<Vehicle> hasBrand (String brand) {
        return (root, query, cb) -> {
            if (brand == null || brand.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
        };
    }

    public static Specification<Vehicle> hasColor (String color) {
        return (root, query, cb) -> {
            if (color == null || color.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("color")), "%" + color.toLowerCase() + "%");
        };
    }

    public static Specification<Vehicle> belongsToUser(String parkUserName) {
        return (root, query, cb) -> {
            if (parkUserName == null) {
                return null;
            }
            return cb.like(cb.lower(root.join("parkUser").get("name")), "%" + parkUserName + "%");
        };
    }
}
