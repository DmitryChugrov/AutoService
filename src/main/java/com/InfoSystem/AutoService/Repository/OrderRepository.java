package com.InfoSystem.AutoService.Repository;

import com.InfoSystem.AutoService.ForAuto.Brand;
import com.InfoSystem.AutoService.Model.Order;
import com.InfoSystem.AutoService.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

//  Интерфейс для обращения к таблице хранения данных о заказах клиентов
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClient(Users client);
    @Query("SELECT o FROM Order o " +
            "JOIN o.auto a " +
            "WHERE (:orderId IS NULL OR o.id = :orderId) AND " +
            "(:brand IS NULL OR a.brand = :brand) AND " +
            "(:model IS NULL OR a.model LIKE %:model%) AND " +
            "(:year IS NULL OR a.year = :year) AND " +
            "(:vin IS NULL OR a.vinNumber LIKE %:vin%) AND " +
            "(:gosNumber IS NULL OR a.GOS_number LIKE %:gosNumber%) AND " +
            "(:date IS NULL OR o.slot.date = :date) AND " +
            "(:clientEmail IS NULL OR o.client.email LIKE %:clientEmail%) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:serviceType IS NULL OR o.serviceType LIKE %:serviceType%)")
    List<Order> findOrdersByFilter(
            @Param("orderId") Long orderId, // ID заказа
            @Param("brand") Brand brand,
            @Param("model") String model,
            @Param("year") Integer year,
            @Param("vin") String vin,
            @Param("gosNumber") String gosNumber,
            @Param("date") LocalDate date,
            @Param("clientEmail") String clientEmail,
            @Param("status") Integer status,
            @Param("serviceType") String serviceType);
    @Query("SELECT o FROM Order o WHERE FUNCTION('MONTH', o.slot.date) = :month AND FUNCTION('YEAR', o.slot.date) = :year")
    List<Order> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}

