package com.InfoSystem.AutoService.Service;

import com.InfoSystem.AutoService.Model.Order;
import com.InfoSystem.AutoService.Repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Класс - сервис для реализации функций для статистики
@Service
public class StatisticsService {

    private final OrderRepository orderRepository;

    public StatisticsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Map<String, Object> calculateMonthlyStatistics(int month, int year) {
        List<Order> orders = orderRepository.findByMonthAndYear(month, year);

        int totalOrders = orders.size();

        double averagePrice = orders.stream()
                .filter(order -> order.getPrice() != null)
                .mapToDouble(Order::getPrice)
                .average()
                .orElse(0.0);
        double totalPrice = orders.stream()
                .filter(order -> order.getPrice() != null)
                .mapToDouble(Order::getPrice)
                .sum();

        Map<LocalDate, Long> ordersPerDay = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getSlot().getDate(),
                        Collectors.counting()
                ));

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("averagePrice", averagePrice);
        statistics.put("totalPrice", totalPrice);
        statistics.put("ordersPerDay", ordersPerDay);

        return statistics;
    }
}

