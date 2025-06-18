package com.InfoSystem.AutoService.ForOrder;

// Вспомогательный класс - enum для статуса заказа
public enum OrderStatus {
    AWAITING_CONFIRMATION(1),
    CONFIRMED(2),
    IN_PROGRESS(3),
    COMPLETED(4);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static OrderStatus fromValue(int value) {
        for (OrderStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status value: " + value);
    }
}

