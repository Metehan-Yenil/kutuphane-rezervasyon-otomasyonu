package com.kutuphanerezervasyon.kutuphane.entity;

import com.kutuphanerezervasyon.kutuphane.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Kullanıcıların oda ve ekipman rezervasyonlarını tutan ana entity
 * Her rezervasyon bir kullanıcıya, bir zaman dilimine ve bir oda/ekipmana bağlıdır
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Kullanıcı boş olamaz")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = true)
    private Room room;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = true)
    private Equipment equipment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    @NotNull(message = "Zaman dilimi boş olamaz")
    private TimeSlot timeSlot;
    
    @NotNull(message = "Rezervasyon tarihi boş olamaz")
    @Column(nullable = false)
    private LocalDate reservationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'BEKLENIYOR'")
    private ReservationStatus status = ReservationStatus.BEKLENIYOR;
    
    /**
     * Rezervasyonun geçerli olup olmadığını kontrol eder
     * En az bir oda veya ekipman seçilmiş olmalı
     */
    public boolean isValid() {
        return (room != null || equipment != null) && user != null && timeSlot != null && reservationDate != null;
    }
    
    /**
     * Rezervasyonu iptal eder
     */
    public void cancel() {
        this.status = ReservationStatus.IPTAL_EDILDI;
    }
    
    /**
     * Rezervasyonu onaylar (admin tarafından)
     */
    public void confirm() {
        this.status = ReservationStatus.ONAYLANDI;
    }
}
