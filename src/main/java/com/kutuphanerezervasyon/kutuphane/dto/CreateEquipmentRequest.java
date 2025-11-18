package com.kutuphanerezervasyon.kutuphane.dto;

import com.kutuphanerezervasyon.kutuphane.enums.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEquipmentRequest {
    
    @NotBlank(message = "Ekipman adı boş olamaz")
    private String name;
    
    @NotBlank(message = "Ekipman türü boş olamaz")
    private String type;
    
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;
}
