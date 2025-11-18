package com.kutuphanerezervasyon.kutuphane.repository;

import com.kutuphanerezervasyon.kutuphane.entity.Reservation;
import com.kutuphanerezervasyon.kutuphane.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    // Kullanıcının rezervasyonlarını getir
    List<Reservation> findByUserUserId(Integer userId);
    
    // Kullanıcının aktif rezervasyonlarını getir
    @Query("SELECT r FROM Reservation r WHERE r.user.userId = :userId " +
           "AND r.status = 'ONAYLANDI' " +
           "AND (r.reservationDate > :today OR " +
           "(r.reservationDate = :today AND r.timeSlot.endTime > :currentTime))")
    List<Reservation> findActiveReservationsByUserId(@Param("userId") Integer userId, 
                                                      @Param("today") LocalDate today,
                                                      @Param("currentTime") java.time.LocalTime currentTime);
    
    // Kullanıcının aktif rezervasyon sayısını al (ONAYLANDI veya BEKLENIYOR)
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.userId = :userId " +
           "AND (r.status = 'ONAYLANDI' OR r.status = 'BEKLENIYOR') " +
           "AND (r.reservationDate > :today OR " +
           "(r.reservationDate = :today AND r.timeSlot.endTime > :currentTime))")
    Long countActiveReservationsByUserId(@Param("userId") Integer userId, 
                                         @Param("today") LocalDate today,
                                         @Param("currentTime") java.time.LocalTime currentTime);
    
    // Oda için rezervasyonları getir
    List<Reservation> findByRoomRoomId(Integer roomId);
    
    // Ekipman için rezervasyonları getir
    List<Reservation> findByEquipmentEquipmentId(Integer equipmentId);
    
    // Belirli tarih için rezervasyonları getir
    List<Reservation> findByReservationDate(LocalDate date);
    
    // Belirli durumdaki rezervasyonları getir
    List<Reservation> findByStatus(ReservationStatus status);
    
    // Çakışma kontrolü - Oda
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.roomId = :roomId " +
           "AND r.reservationDate = :date " +
           "AND r.timeSlot.timeSlotId = :timeSlotId " +
           "AND r.status = 'ONAYLANDI'")
    Long checkRoomConflict(@Param("roomId") Integer roomId, 
                           @Param("date") LocalDate date, 
                           @Param("timeSlotId") Integer timeSlotId);
    
    // Çakışma kontrolü - Ekipman
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.equipment.equipmentId = :equipmentId " +
           "AND r.reservationDate = :date " +
           "AND r.timeSlot.timeSlotId = :timeSlotId " +
           "AND r.status = 'ONAYLANDI'")
    Long checkEquipmentConflict(@Param("equipmentId") Integer equipmentId, 
                                @Param("date") LocalDate date, 
                                @Param("timeSlotId") Integer timeSlotId);
    
    // Tüm bekleyen rezervasyonları getir (Admin için)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'BEKLENIYOR' ORDER BY r.reservationDate, r.timeSlot.startTime")
    List<Reservation> findPendingReservations();
}
