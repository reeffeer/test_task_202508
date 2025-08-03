package ru.yandex.service;

import ru.yandex.enums.Dimension;
import ru.yandex.enums.Workload;

import static ru.yandex.enums.Dimension.BIG;
import static ru.yandex.enums.Dimension.SMALL;
import static ru.yandex.enums.Workload.*;

public class DeliveryService {
    private static final double minCost = 400;

    public double calculateDeliveryCost(
            double distance,
            Dimension dimension,
            boolean fragility,
            Workload workload) {
        double cost = checkDistance(distance);
        cost += costcheckDimension(dimension);
        cost += checkFragility(fragility, distance);
        cost *= checkMultiplier(workload);

        return Math.max(cost, minCost);
    }

    private double checkMultiplier(Workload workload) {
        return switch (workload) {
            case HIGHEST -> HIGHEST.getValue();
            case HIGHER -> HIGHER.getValue();
            case HIGH -> HIGH.getValue();
            default -> NORMAL.getValue();
        };
    }

    private double checkFragility(boolean fragility, double distance) {
        if (fragility) {
            if (distance > 30) {
                throw new IllegalArgumentException(
                        "Fragile items can't be shipped over 30 km");
            }
            return 300;
        }
        return 0;
    }

    private double costcheckDimension(Dimension dimension) {
        return switch (dimension) {
            case BIG -> 200;
            case SMALL -> 100;
        };
    }

    private double checkDistance(double distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }else if (distance > 30) {
           return 300;
       } else if (distance > 10) {
           return 200;
       } else if (distance > 2) {
           return 100;
       } else {
           return 50;
       }
   }
}
