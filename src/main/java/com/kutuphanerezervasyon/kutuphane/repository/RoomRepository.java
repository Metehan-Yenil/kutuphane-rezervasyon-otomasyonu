package com.kutuphanerezervasyon.kutuphane.repository;

import com.kutuphanerezervasyon.kutuphane.entity.Room;
import com.kutuphanerezervasyon.kutuphane.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    
    List<Room> findByStatus(RoomStatus status);
    
    List<Room> findByCapacityGreaterThanEqual(Integer capacity);
    
    @Query("SELECT r FROM Room r WHERE r.name LIKE %:keyword%")
    List<Room> searchByName(String keyword);
    
    // Belirli tarih ve zaman diliminde müsait odaları bul
    @Query("SELECT r FROM Room r WHERE r.roomId NOT IN " +
           "(SELECT res.room.roomId FROM Reservation res " +
           "WHERE res.reservationDate = :date " +
           "AND res.timeSlot.timeSlotId = :timeSlotId " +
           "AND res.status = 'ONAYLANDI')")
    List<Room> findAvailableRooms(@Param("date") LocalDate date, 
                                   @Param("timeSlotId") Integer timeSlotId);
}
