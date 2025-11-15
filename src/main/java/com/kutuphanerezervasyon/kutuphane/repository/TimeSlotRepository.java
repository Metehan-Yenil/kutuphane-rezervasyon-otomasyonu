package com.kutuphanerezervasyon.kutuphane.repository;

import com.kutuphanerezervasyon.kutuphane.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    
    Optional<TimeSlot> findByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);
    
    @Query("SELECT ts FROM TimeSlot ts ORDER BY ts.startTime")
    List<TimeSlot> findAllOrderByStartTime();
    
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.startTime >= :startTime AND ts.endTime <= :endTime")
    List<TimeSlot> findTimeSlotsInRange(LocalTime startTime, LocalTime endTime);
}
