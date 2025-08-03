package ru.yandex.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Workload {
    HIGHEST(1.6),
    HIGHER(1.4),
    HIGH(1.2),
    NORMAL(1.0);

    private final double value;
}
