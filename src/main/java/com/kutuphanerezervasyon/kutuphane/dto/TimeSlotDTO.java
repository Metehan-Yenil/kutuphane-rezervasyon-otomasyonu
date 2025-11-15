package com.kutuphanerezervasyon.kutuphane.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private Integer timeSlotId;
    private LocalTime startTime;
    private LocalTime endTime;
}
