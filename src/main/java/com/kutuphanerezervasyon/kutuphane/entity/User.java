package com.kutuphanerezervasyon.kutuphane.entity;

import com.kutuphanerezervasyon.kutuphane.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Kullanıcı bilgilerini tutan entity sınıfı
 * Sistem kullanıcıları (öğrenci, personel, admin) bu tabloda saklanır
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @NotBlank(message = "İsim boş olamaz")
    @Size(max = 100, message = "İsim en fazla 100 karakter olabilir")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    @Size(max = 100, message = "Email en fazla 100 karakter olabilir")
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @NotBlank(message = "Şifre boş olamaz")
    @Size(max = 100, message = "Şifre en fazla 100 karakter olabilir")
    @Column(nullable = false, length = 100)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'USER'")
    private UserRole role = UserRole.USER;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
}
