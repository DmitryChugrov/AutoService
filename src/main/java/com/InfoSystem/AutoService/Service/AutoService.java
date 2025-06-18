package com.InfoSystem.AutoService.Service;

import com.InfoSystem.AutoService.Model.Auto;
import com.InfoSystem.AutoService.Model.Users;
import com.InfoSystem.AutoService.Repository.AutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Класс - сервис для реализации функций, связанных с обращениями к таблице с данными об автомобилях клиентов
@Service
public class AutoService {

    @Autowired
    private AutoRepository autoRepository;

    public void saveAuto(Auto auto) {
        autoRepository.save(auto);
    }

    public boolean isVinExists(String vinNumber) {
        return autoRepository.findByVinNumber(vinNumber) != null;
    }
    public List<Auto> findByUser(Users user) {
        return autoRepository.findByUser(user);
    }
    public List<Auto> searchAutos(String brand, String model, String vinNumber, String gosNumber, String email) {
        return autoRepository.findBySearchQuery(brand, model, vinNumber, gosNumber, email);
    }
    public List<Auto> getAllAutos() {
        return autoRepository.findAll();
    }
}


