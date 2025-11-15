package com.kutuphanerezervasyon.kutuphane.repository;

import com.kutuphanerezervasyon.kutuphane.entity.Equipment;
import com.kutuphanerezervasyon.kutuphane.enums.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    
    List<Equipment> findByStatus(EquipmentStatus status);
    
    List<Equipment> findByType(String type);
    
    @Query("SELECT e FROM Equipment e WHERE e.name LIKE %:keyword% OR e.type LIKE %:keyword%")
    List<Equipment> searchEquipment(String keyword);
    
    // Belirli tarih ve zaman diliminde müsait ekipmanları bul
    @Query("SELECT e FROM Equipment e WHERE e.equipmentId NOT IN " +
           "(SELECT res.equipment.equipmentId FROM Reservation res " +
           "WHERE res.reservationDate = :date " +
           "AND res.timeSlot.timeSlotId = :timeSlotId " +
           "AND res.status = 'ONAYLANDI')")
    List<Equipment> findAvailableEquipment(@Param("date") LocalDate date, 
                                           @Param("timeSlotId") Integer timeSlotId);
}
