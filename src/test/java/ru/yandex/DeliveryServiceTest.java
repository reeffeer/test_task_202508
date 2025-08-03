package ru.yandex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.enums.Dimension;
import ru.yandex.enums.Workload;
import ru.yandex.service.DeliveryService;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.enums.Dimension.BIG;
import static ru.yandex.enums.Dimension.SMALL;
import static ru.yandex.enums.Workload.*;

public class DeliveryServiceTest {
    private DeliveryService deliveryService;

    @BeforeEach
    public void setUp() {
        deliveryService = new DeliveryService();
    }

    @ParameterizedTest(name = "check cost calculation")
    @Tag("positive")
    @Tag("regress")
    @MethodSource("provideValidDeliveryCostCases")
    @DisplayName("calculateDeliveryCost: позитивные сценарии")
    void checkCalculationExpectedCost(double distance, Dimension dimension, boolean fragility,
            Workload workload, BigDecimal expected) {

        BigDecimal actual = deliveryService.calculateDeliveryCost(
                distance, dimension, fragility, workload);

        assertEquals(0, expected.compareTo(actual), () -> String.format(
                "Expected result %s but got %s for parameters: distance=%.1f, dimension=%s, fragility=%b, workload=%.1f",
                expected, actual, distance, dimension, fragility, workload.getValue()));
    }

    static Stream<Arguments>
    provideValidDeliveryCostCases() {
        return Stream.of(
                Arguments.of(0, SMALL, false, NORMAL, BigDecimal.ZERO),
                Arguments.of(2.0, SMALL, false, NORMAL, BigDecimal.valueOf(400)),
                Arguments.of(2.1, SMALL, false, HIGHER, BigDecimal.valueOf(400)),
                Arguments.of(10.0, BIG, false, HIGH, BigDecimal.valueOf(400)),
                Arguments.of(10.1, BIG, true, NORMAL, BigDecimal.valueOf(700)),
                Arguments.of(30.0, SMALL, true, HIGH, BigDecimal.valueOf(720)),
                Arguments.of(30.1, BIG, false, HIGHEST, BigDecimal.valueOf(800))
                );
    }

    @Test
    @Tag("negative")
    @DisplayName("Отрицательное расстояние → Exception")
    void checkWhenNegativeDistance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                deliveryService.calculateDeliveryCost(
                        -0.1, SMALL, false, NORMAL));
        assertEquals("Distance cannot be negative", exception.getMessage());
    }

    @Test
    @Tag("negative")
    @DisplayName("Хрупкий && distance > 30 → Exception")
    void checkWhenFragileAndDistanceGt30() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                deliveryService.calculateDeliveryCost(
                        31.0, SMALL, true, NORMAL));
        assertEquals("Fragile items can't be shipped over 30 km", exception.getMessage());
    }
}
