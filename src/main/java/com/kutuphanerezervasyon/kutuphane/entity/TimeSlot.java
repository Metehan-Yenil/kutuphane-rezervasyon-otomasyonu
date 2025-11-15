package com.kutuphanerezervasyon.kutuphane.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/*
 Rezervasyon için kullanılabilecek zaman dilimlerini tanımlayan entity
 Örn: 09:00-10:00, 10:00-11:30
 */
@Entity
@Table(name = "time_slots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_slot_id")
    private Integer timeSlotId;
    
    @NotNull(message = "Başlangıç saati boş olamaz")
    @Column(nullable = false)
    private LocalTime startTime;
    
    @NotNull(message = "Bitiş saati boş olamaz")
    @Column(nullable = false)
    private LocalTime endTime;
    
    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
    
    // Validation method
    public boolean isValid() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}
