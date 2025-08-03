package ru.yandex;

import ru.yandex.enums.Dimension;
import ru.yandex.enums.Workload;
import ru.yandex.service.DeliveryService;

public class Main {

    public static void main(String[] args) {
        DeliveryService deliveryService = new DeliveryService();
        System.out.println(deliveryService.calculateDeliveryCost(30.0, Dimension.SMALL, true, Workload.HIGH));
    }
}
