package com.InfoSystem.AutoService.Model;

import com.InfoSystem.AutoService.ForAuto.Brand;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.sql.Date;

//  Класс - модель для таблицы хранения данных об автомобилях клиентов
@Entity
@Table(name = "auto_autoservice")
public class Auto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Поле не должно быть пустым")
    private Brand brand;
    @NotBlank(message = "Поле не должно быть пустым")
    private String model;
    @NotBlank(message = "Поле не должно быть пустым")
    private String generation;
    @NotBlank(message = "Поле не должно быть пустым")
    @Min(value = 1949, message = "Год должен быть не меньше 1900")
    @Max(value = 2099, message = "Год должен быть не больше 2099")
    private int year;
    @NotBlank(message = "Поле не должно быть пустым")
    @Max(value = 17, message = "VIN должен содержать 17 символов")
    @Min(value = 17, message = "VIN должен содержать 17 символов")
    @Size(min = 17, max = 17, message = "VIN должен содержать 17 символов")
    @Pattern(regexp = "^[0-9A-HJ-NPR-Z]{17}$", message = "VIN должен содержать только допустимые символы и быть длиной 17 символов")
    private String vinNumber;
    @NotBlank(message = "Поле не должно быть пустым")
    @Max(value = 9, message = "Госномер должен содержать 8 или 9 символов")
    @Min(value = 8, message = "Госномер должен содержать 8 или 9 символов")
    @Size(min = 8, max = 9, message = "Госномер должен содержать от 8 до 9 символов")
    @Pattern(regexp = "[АВЕКМНОРСТУХ]{1}[0-9]{3}[АВЕКМНОРСТУХ]{2}[0-9]{2,3}",
            message = "Госномер должен быть в формате: одна буква, три цифры, две буквы, и номер региона (2 или 3 цифры)")
    private String GOS_number;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getGOS_number() {
        return GOS_number;
    }

    public void setGOS_number(String GOS_number) {
        this.GOS_number = GOS_number;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
