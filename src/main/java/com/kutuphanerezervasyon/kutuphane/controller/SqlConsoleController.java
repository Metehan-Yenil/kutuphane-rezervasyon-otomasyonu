package com.kutuphanerezervasyon.kutuphane.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/sql")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201", "http://localhost:3000"})
// @PreAuthorize("hasRole('ADMIN')") // Test için geçici olarak kapatıldı
public class SqlConsoleController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/execute")
    public ResponseEntity<?> executeSql(@RequestBody SqlQueryRequest request) {
        try {
            String query = request.getQuery().trim();
            String queryUpper = query.toUpperCase();
            
            // GÜVENLİK: Sadece SELECT sorgularına izin ver
            if (!queryUpper.startsWith("SELECT")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Sadece SELECT sorguları çalıştırılabilir!"));
            }
            
            // DML/DDL engelle
            if (queryUpper.contains("DROP") || queryUpper.contains("DELETE") || 
                queryUpper.contains("UPDATE") || queryUpper.contains("INSERT") || 
                queryUpper.contains("ALTER") || queryUpper.contains("CREATE") || 
                queryUpper.contains("TRUNCATE") || queryUpper.contains("GRANT") ||
                queryUpper.contains("REVOKE")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "DML/DDL sorguları engellenmiştir!"));
            }

            // Query timeout ayarla (5 saniye)
            jdbcTemplate.setQueryTimeout(5);
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
            
            // Max row limit (1000)
            if (results.size() > 1000) {
                results = results.subList(0, 1000);
            }
            
            if (results.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "columns", new ArrayList<>(),
                    "rows", new ArrayList<>(),
                    "rowCount", 0,
                    "message", "Sorgu başarılı ama sonuç bulunamadı"
                ));
            }

            // Sütun adlarını al
            List<String> columns = new ArrayList<>(results.get(0).keySet());
            
            // Satırları liste formatına çevir
            List<List<Object>> rows = new ArrayList<>();
            for (Map<String, Object> row : results) {
                List<Object> rowValues = new ArrayList<>();
                for (String column : columns) {
                    Object value = row.get(column);
                    rowValues.add(value != null ? value : "NULL");
                }
                rows.add(rowValues);
            }

            return ResponseEntity.ok(Map.of(
                "columns", columns,
                "rows", rows,
                "rowCount", results.size(),
                "message", results.size() + " satır döndürüldü"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "SQL Hatası: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tables")
    public ResponseEntity<?> listTables() {
        try {
            String query = "SELECT table_name FROM information_schema.tables " +
                          "WHERE table_schema = 'public' ORDER BY table_name";
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
            List<String> tables = new ArrayList<>();
            
            for (Map<String, Object> row : results) {
                tables.add((String) row.get("table_name"));
            }
            
            return ResponseEntity.ok(Map.of("tables", tables));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Tablo listesi alınamadı: " + e.getMessage()));
        }
    }
    
    @GetMapping("/columns/{tableName}")
    public ResponseEntity<?> getTableColumns(@PathVariable String tableName) {
        try {
            // SQL injection koruması
            if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Geçersiz tablo adı!"));
            }
            
            String query = "SELECT column_name, data_type, is_nullable " +
                          "FROM information_schema.columns " +
                          "WHERE table_schema = 'public' AND table_name = ? " +
                          "ORDER BY ordinal_position";
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query, tableName);
            
            return ResponseEntity.ok(Map.of("columns", results));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Sütun bilgileri alınamadı: " + e.getMessage()));
        }
    }
}

// DTO Class - Controller dışında!
class SqlQueryRequest {
    private String query;

    public SqlQueryRequest() {} // Default constructor gerekli!

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}