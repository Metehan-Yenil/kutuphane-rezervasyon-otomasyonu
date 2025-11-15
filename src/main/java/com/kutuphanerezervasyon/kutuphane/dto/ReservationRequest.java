package com.kutuphanerezervasyon.kutuphane.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    
    @NotNull(message = "Kullanıcı ID boş olamaz")
    private Integer userId;
    
    private Integer roomId;      // Nullable - Room veya Equipment'tan biri olmalı
    
    private Integer equipmentId; // Nullable - Room veya Equipment'tan biri olmalı
    
    @NotNull(message = "Zaman dilimi ID boş olamaz")
    private Integer timeSlotId;
    
    @NotNull(message = "Rezervasyon tarihi boş olamaz")
    private LocalDate reservationDate;
}
