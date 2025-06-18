package com.InfoSystem.AutoService.ForStats;

import java.util.Map;

//  Вспомогательный класс для формирования статистики
public class OrderStatistics {
    private int totalOrders;
    private double averagePrice;
    private Map<Integer, Long> dailyCounts;

    public OrderStatistics(int totalOrders, double averagePrice, Map<Integer, Long> dailyCounts) {
        this.totalOrders = totalOrders;
        this.averagePrice = averagePrice;
        this.dailyCounts = dailyCounts;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public Map<Integer, Long> getDailyCounts() {
        return dailyCounts;
    }
}

