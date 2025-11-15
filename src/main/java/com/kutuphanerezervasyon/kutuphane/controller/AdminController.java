package com.kutuphanerezervasyon.kutuphane.controller;

import com.kutuphanerezervasyon.kutuphane.dto.*;
import com.kutuphanerezervasyon.kutuphane.enums.EquipmentStatus;
import com.kutuphanerezervasyon.kutuphane.enums.RoomStatus;
import com.kutuphanerezervasyon.kutuphane.enums.UserRole;
import com.kutuphanerezervasyon.kutuphane.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin paneli endpoint'leri
 * Sistem istatistikleri ve bekleyen rezervasyonları onaylama/reddetme
 * Sadece ADMIN rolüne sahip kullanıcılar erişebilir
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AdminController {

    private final UserService userService;
    private final RoomService roomService;
    private final EquipmentService equipmentService;
    private final ReservationService reservationService;
    private final TimeSlotService timeSlotService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Kullanıcı istatistikleri
        List<UserDTO> allUsers = userService.getAllUsers();
        List<UserDTO> adminUsers = userService.getUsersByRole(UserRole.ADMIN);
        stats.put("totalUsers", allUsers.size());
        stats.put("totalAdmins", adminUsers.size());
        stats.put("totalRegularUsers", allUsers.size() - adminUsers.size());
        
        // Oda istatistikleri
        List<RoomDTO> allRooms = roomService.getAllRooms();
        List<RoomDTO> emptyRooms = roomService.getRoomsByStatus(RoomStatus.EMPTY);
        List<RoomDTO> occupiedRooms = roomService.getRoomsByStatus(RoomStatus.OCCUPIED);
        List<RoomDTO> maintenanceRooms = roomService.getRoomsByStatus(RoomStatus.MAINTENANCE);
        stats.put("totalRooms", allRooms.size());
        stats.put("emptyRooms", emptyRooms.size());
        stats.put("occupiedRooms", occupiedRooms.size());
        stats.put("maintenanceRooms", maintenanceRooms.size());
        
        // Ekipman istatistikleri
        List<EquipmentDTO> allEquipment = equipmentService.getAllEquipment();
        List<EquipmentDTO> availableEquipment = equipmentService.getEquipmentByStatus(EquipmentStatus.AVAILABLE);
        List<EquipmentDTO> reservedEquipment = equipmentService.getEquipmentByStatus(EquipmentStatus.RESERVED);
        List<EquipmentDTO> maintenanceEquipment = equipmentService.getEquipmentByStatus(EquipmentStatus.MAINTENANCE);
        stats.put("totalEquipment", allEquipment.size());
        stats.put("availableEquipment", availableEquipment.size());
        stats.put("reservedEquipment", reservedEquipment.size());
        stats.put("maintenanceEquipment", maintenanceEquipment.size());
        
        // Rezervasyon istatistikleri
        List<ReservationDTO> allReservations = reservationService.getAllReservations();
        List<ReservationDTO> pendingReservations = reservationService.getPendingReservations();
        stats.put("totalReservations", allReservations.size());
        stats.put("pendingReservations", pendingReservations.size());
        
        // Zaman dilimi istatistikleri
        List<TimeSlotDTO> allTimeSlots = timeSlotService.getAllTimeSlots();
        stats.put("totalTimeSlots", allTimeSlots.size());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/equipment")
    public ResponseEntity<List<EquipmentDTO>> getAllEquipment() {
        List<EquipmentDTO> equipment = equipmentService.getAllEquipment();
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/reservations/pending")
    public ResponseEntity<List<ReservationDTO>> getPendingReservations() {
        List<ReservationDTO> reservations = reservationService.getPendingReservations();
        return ResponseEntity.ok(reservations);
    }

    @PatchMapping("/reservations/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Integer id) {
        ReservationDTO reservation = reservationService.confirmReservation(id);
        return ResponseEntity.ok(reservation);
    }

    @PatchMapping("/rooms/{id}/status")
    public ResponseEntity<RoomDTO> updateRoomStatus(
            @PathVariable Integer id,
            @RequestParam RoomStatus status) {
        RoomDTO room = roomService.updateRoomStatus(id, status);
        return ResponseEntity.ok(room);
    }

    @PatchMapping("/equipment/{id}/status")
    public ResponseEntity<EquipmentDTO> updateEquipmentStatus(
            @PathVariable Integer id,
            @RequestParam EquipmentStatus status) {
        EquipmentDTO equipment = equipmentService.updateEquipmentStatus(id, status);
        return ResponseEntity.ok(equipment);
    }

    @PutMapping("/users/{id}/promote")
    public ResponseEntity<UserDTO> promoteToAdmin(@PathVariable Integer id) {
        UserDTO user = userService.promoteToAdmin(id);
        return ResponseEntity.ok(user);
    }
}
