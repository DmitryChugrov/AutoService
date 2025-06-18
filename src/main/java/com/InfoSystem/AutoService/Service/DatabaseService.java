package com.InfoSystem.AutoService.Service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Класс - сервис для реализации функций обращений с данными о таблицах и их содержании в БД
@Service
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getAllTables() {
        return jdbcTemplate.queryForList("SHOW TABLES", String.class);
    }
    public List<List<Object>> getTableData(String tableName) {
        String query = "SELECT * FROM " + tableName;
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            int columnCount = rs.getMetaData().getColumnCount();
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            return row;
        });
    }
}

