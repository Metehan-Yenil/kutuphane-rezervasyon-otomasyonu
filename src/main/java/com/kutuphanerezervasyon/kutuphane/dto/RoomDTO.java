package com.kutuphanerezervasyon.kutuphane.dto;

import com.kutuphanerezervasyon.kutuphane.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Integer roomId;
    private String name;
    private Integer capacity;
    private RoomStatus status;
}
