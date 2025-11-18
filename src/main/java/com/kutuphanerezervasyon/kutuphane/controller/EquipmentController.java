package com.kutuphanerezervasyon.kutuphane.controller;

import com.kutuphanerezervasyon.kutuphane.dto.EquipmentDTO;
import com.kutuphanerezervasyon.kutuphane.enums.EquipmentStatus;
import com.kutuphanerezervasyon.kutuphane.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/*
 Ekipman yönetimi endpoint'leri
 Laptop, projeksiyon gibi cihazların CRUD işlemleri ve müsaitlik sorgulama
 Örnek: GET /api/equipment/available?date=2024-01-15&timeSlotId=1
 */
@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201", "http://localhost:3000"})
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDTO> createEquipment(@Valid @RequestBody EquipmentDTO equipmentDTO) {
        EquipmentDTO equipment = equipmentService.createEquipment(equipmentDTO);
        return new ResponseEntity<>(equipment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDTO> getEquipmentById(@PathVariable Integer id) {
        EquipmentDTO equipment = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAllEquipment() {
        List<EquipmentDTO> equipment = equipmentService.getAllEquipment();
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EquipmentDTO>> getEquipmentByStatus(@PathVariable EquipmentStatus status) {
        List<EquipmentDTO> equipment = equipmentService.getEquipmentByStatus(status);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<EquipmentDTO>> getEquipmentByType(@PathVariable String type) {
        List<EquipmentDTO> equipment = equipmentService.getEquipmentByType(type);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EquipmentDTO>> searchEquipment(@RequestParam String keyword) {
        List<EquipmentDTO> equipment = equipmentService.searchEquipment(keyword);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/available")
    public ResponseEntity<List<EquipmentDTO>> getAvailableEquipment(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer timeSlotId) {
        List<EquipmentDTO> equipment = equipmentService.getAvailableEquipment(date, timeSlotId);
        return ResponseEntity.ok(equipment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDTO> updateEquipment(
            @PathVariable Integer id,
            @Valid @RequestBody EquipmentDTO equipmentDTO) {
        EquipmentDTO updatedEquipment = equipmentService.updateEquipment(id, equipmentDTO);
        return ResponseEntity.ok(updatedEquipment);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDTO> updateEquipmentStatus(
            @PathVariable Integer id,
            @RequestParam EquipmentStatus status) {
        EquipmentDTO updatedEquipment = equipmentService.updateEquipmentStatus(id, status);
        return ResponseEntity.ok(updatedEquipment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Integer id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}
