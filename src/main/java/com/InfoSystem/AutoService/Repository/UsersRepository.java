package com.InfoSystem.AutoService.Repository;

import com.InfoSystem.AutoService.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//  Интерфейс для обращения к таблице хранения данных пользователей
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    Users findByEmail(String email);
    @Query("SELECT u FROM Users u WHERE u.role = 'CLIENT' AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR u.phone_number LIKE CONCAT('%', :query, '%'))")
    List<Users> findClientsBySearchQuery(@Param("query") String query);

    @Query("SELECT u FROM Users u WHERE u.role = :role")
    List<Users> findByRole(@Param("role") String role);
    @Query("SELECT u FROM Users u WHERE u.role IN :roles")
    List<Users> findPersonnelByRoles(@Param("roles") List<String> roles);

    @Query("SELECT u FROM Users u WHERE u.role IN :roles AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR u.phone_number LIKE CONCAT('%', :query, '%'))")
    List<Users> searchPersonnelByQuery(@Param("query") String query, @Param("roles") List<String> roles);
}

