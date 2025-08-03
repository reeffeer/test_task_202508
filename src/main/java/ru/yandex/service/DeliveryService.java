package ru.yandex.service;

import lombok.NonNull;
import ru.yandex.enums.Dimension;
import ru.yandex.enums.Workload;

import java.math.BigDecimal;
import java.util.Objects;

import static ru.yandex.enums.Workload.NORMAL;
import static ru.yandex.enums.Workload.HIGH;
import static ru.yandex.enums.Workload.HIGHER;
import static ru.yandex.enums.Workload.HIGHEST;

public class DeliveryService {
    private static final BigDecimal MIN_COST = BigDecimal.valueOf(400);
    private static final double BAND1 = 2.0;
    private static final double BAND2 = 10.0;
    private static final double BAND3 = 30.0;

    public BigDecimal calculateDeliveryCost(
            double distance,
            @NonNull Dimension dimension,
            boolean fragility,
            @NonNull Workload workload) {
        Objects.requireNonNull(dimension, "Dimension is required");
        Objects.requireNonNull(workload, "Workload is required");

        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        if (distance == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal cost = BigDecimal.ZERO
                .add(BigDecimal.valueOf(checkDistance(distance)))
                .add(BigDecimal.valueOf(checkDimension(dimension)))
                .add(checkFragility(fragility, distance));

        cost = cost.multiply(BigDecimal.valueOf(workload.getValue()));

        return cost.max(MIN_COST);
    }

    private double checkMultiplier(Workload workload) {
        return switch (workload) {
            case HIGHEST -> HIGHEST.getValue();
            case HIGHER -> HIGHER.getValue();
            case HIGH -> HIGH.getValue();
            default -> NORMAL.getValue();
        };
    }

    private BigDecimal checkFragility(boolean fragility, double distance) {
        if (fragility) {
            if (distance > 30) {
                throw new IllegalArgumentException("Fragile items can't be shipped over 30 km");
            }
            return BigDecimal.valueOf(300);
        }
        return BigDecimal.ZERO;
    }

    private double checkDimension(Dimension dimension) {
        return switch (dimension) {
            case BIG -> 200;
            case SMALL -> 100;
        };
    }

    private double checkDistance(double distance) {
        if (distance > BAND3) {
           return 300;
       } else if (distance > BAND2) {
           return 200;
       } else if (distance > BAND1) {
           return 100;
       } else {
           return 50;
       }
   }
}
