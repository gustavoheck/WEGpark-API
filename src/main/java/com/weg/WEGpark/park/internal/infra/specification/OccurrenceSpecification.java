package com.weg.WEGpark.park.internal.infra.specification;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public class OccurrenceSpecification {

    public static Specification<Occurrence> hasLocal(String local) {
        return (root, query, cb) -> {
           if (local == null || local.trim().isEmpty()) {
               return null;
           }
           return cb.like(cb.lower(root.get("location")), "%" + local.toLowerCase() + "%");
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
            return cb.like(cb.lower(root.get("occurrence_type")), "%" + type.toLowerCase() + "%");
        };
    }

    public static Specification<Occurrence> hasDate(YearMonth yearMonth) {
        return (root, query, cb) -> {
            if (yearMonth == null) {
                return null;
            }

            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

            return cb.between(root.get("date_hour"), startOfMonth, endOfMonth);
        };
    }

    public static Specification<Occurrence> hasRecents (Boolean searchRecents) {
        return (root, query, cb) -> {
            if (searchRecents == null || !searchRecents) {
                return null;
            }

            LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = YearMonth.now().atEndOfMonth().atTime(LocalTime.MAX);

            return cb.between(root.get("date_hour"), startOfMonth, endOfMonth);
        };
    }
}
