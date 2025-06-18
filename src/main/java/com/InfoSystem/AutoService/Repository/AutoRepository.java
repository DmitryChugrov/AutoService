package com.InfoSystem.AutoService.Repository;

import com.InfoSystem.AutoService.Model.Auto;
import com.InfoSystem.AutoService.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//  Интерфейс для обращения к таблице хранения данных об автомобилях клиентов
public interface AutoRepository extends JpaRepository<Auto, Long> {
    Auto findByVinNumber(String vinNumber);
    List<Auto> findByUser(Users user);
    @Query("SELECT a FROM Auto a WHERE " +
            "(LOWER(a.brand) LIKE LOWER(CONCAT('%', :brand, '%')) OR :brand IS NULL) AND " +
            "(LOWER(a.model) LIKE LOWER(CONCAT('%', :model, '%')) OR :model IS NULL) AND " +
            "(LOWER(a.vinNumber) LIKE LOWER(CONCAT('%', :vinNumber, '%')) OR :vinNumber IS NULL) AND " +
            "(LOWER(a.GOS_number) LIKE LOWER(CONCAT('%', :gosNumber, '%')) OR :gosNumber IS NULL) AND " +
            "(LOWER(a.user.email) LIKE LOWER(CONCAT('%', :email, '%')) OR :email IS NULL)")
    List<Auto> findBySearchQuery(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("vinNumber") String vinNumber,
            @Param("gosNumber") String gosNumber,
            @Param("email") String email
    );
}
