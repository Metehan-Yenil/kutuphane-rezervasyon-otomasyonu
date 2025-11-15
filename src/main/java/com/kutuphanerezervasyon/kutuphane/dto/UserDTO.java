package com.kutuphanerezervasyon.kutuphane.dto;

import com.kutuphanerezervasyon.kutuphane.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer userId;
    
    @NotBlank(message = "İsim boş olamaz")
    @Size(max = 100)
    private String name;
    
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;
    
    private UserRole role;
}
