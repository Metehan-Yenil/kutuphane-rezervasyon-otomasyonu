package com.kutuphanerezervasyon.kutuphane.controller;

import com.kutuphanerezervasyon.kutuphane.dto.TimeSlotDTO;
import com.kutuphanerezervasyon.kutuphane.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

/*
 Zaman dilimi yönetimi endpoint'leri
  Rezervasyon için kullanılabilecek sabit zaman dilimlerini yönetme
  Örnek: 09:00-10:00 gibi zaman aralıkları tanımlama
 */
@RestController
@RequestMapping("/api/timeslots")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201", "http://localhost:3000"})
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimeSlotDTO> createTimeSlot(@Valid @RequestBody TimeSlotDTO timeSlotDTO) {
        TimeSlotDTO timeSlot = timeSlotService.createTimeSlot(timeSlotDTO);
        return new ResponseEntity<>(timeSlot, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotDTO> getTimeSlotById(@PathVariable Integer id) {
        TimeSlotDTO timeSlot = timeSlotService.getTimeSlotById(id);
        return ResponseEntity.ok(timeSlot);
    }

    @GetMapping
    public ResponseEntity<List<TimeSlotDTO>> getAllTimeSlots() {
        List<TimeSlotDTO> timeSlots = timeSlotService.getAllTimeSlots();
        return ResponseEntity.ok(timeSlots);
    }

    @GetMapping("/range")
    public ResponseEntity<List<TimeSlotDTO>> getTimeSlotsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        List<TimeSlotDTO> timeSlots = timeSlotService.getTimeSlotsInRange(startTime, endTime);
        return ResponseEntity.ok(timeSlots);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimeSlotDTO> updateTimeSlot(
            @PathVariable Integer id,
            @Valid @RequestBody TimeSlotDTO timeSlotDTO) {
        TimeSlotDTO updatedTimeSlot = timeSlotService.updateTimeSlot(id, timeSlotDTO);
        return ResponseEntity.ok(updatedTimeSlot);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Integer id) {
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }
}
