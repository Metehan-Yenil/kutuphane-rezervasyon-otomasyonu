package com.kutuphanerezervasyon.kutuphane.dto;

import com.kutuphanerezervasyon.kutuphane.enums.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    
    @NotBlank(message = "Oda adı boş olamaz")
    private String name;
    
    @NotNull(message = "Kapasite boş olamaz")
    @Min(value = 1, message = "Kapasite en az 1 olmalıdır")
    private Integer capacity;
    
    private RoomStatus status = RoomStatus.EMPTY;
}
