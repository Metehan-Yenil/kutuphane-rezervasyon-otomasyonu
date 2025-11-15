package com.kutuphanerezervasyon.kutuphane.dto;

import com.kutuphanerezervasyon.kutuphane.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Integer reservationId;
    private UserDTO user;
    private RoomDTO room;
    private EquipmentDTO equipment;
    private TimeSlotDTO timeSlot;
    private LocalDate reservationDate;
    private ReservationStatus status;
}
