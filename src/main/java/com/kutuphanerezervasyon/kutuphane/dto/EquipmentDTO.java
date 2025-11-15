package com.kutuphanerezervasyon.kutuphane.dto;

import com.kutuphanerezervasyon.kutuphane.enums.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO {
    private Integer equipmentId;
    private String name;
    private String type;
    private EquipmentStatus status;
}
