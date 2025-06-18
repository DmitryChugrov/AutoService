package com.InfoSystem.AutoService.Repository;

import com.InfoSystem.AutoService.Model.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

//  Интерфейс для обращения к таблице хранения данных о доступных слотах для записи
public interface ScheduleSlotRepository extends JpaRepository<ScheduleSlot, Long> {
    List<ScheduleSlot> findByAvailableTrue();
    List<ScheduleSlot> findByDateAndAvailableTrue(LocalDate date);
    boolean existsByDateAndTime(LocalDate date, LocalTime time);
}
