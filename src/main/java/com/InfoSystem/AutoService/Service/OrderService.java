package com.InfoSystem.AutoService.Service;

import com.InfoSystem.AutoService.ForAuto.Brand;
import com.InfoSystem.AutoService.ForStats.OrderStatistics;
import com.InfoSystem.AutoService.Model.Order;
import com.InfoSystem.AutoService.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Класс - сервис для реализации функций, связанных с обращениями к таблице с данными о заказах клиентов
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order findById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElseThrow(() -> new RuntimeException("Order not found"));
    }
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElse(null);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void updateOrderPrice(Long orderId, Double price) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Заказ с ID " + orderId + " не найден"));
    order.setPrice(price);
    orderRepository.save(order);
}
    public OrderStatistics getMonthlyStatistics(int month, int year) {
        List<Order> orders = orderRepository.findByMonthAndYear(month, year);

        int totalOrders = orders.size();
        double averagePrice = orders.stream()
                .filter(order -> order.getPrice() != null)
                .mapToDouble(Order::getPrice)
                .average()
                .orElse(0);

        Map<Integer, Long> dailyCounts = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getSlot().getDate().getDayOfMonth(),
                        Collectors.counting()
                ));

        return new OrderStatistics(totalOrders, averagePrice, dailyCounts);
    }
}
