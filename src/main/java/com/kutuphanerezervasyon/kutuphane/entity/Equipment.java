package com.kutuphanerezervasyon.kutuphane.entity;

import com.kutuphanerezervasyon.kutuphane.enums.EquipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/*
  Kütüphanede ödünç alınabilecek ekipmanları temsil eden entity
  Laptop, projeksiyon cihazı, tablet gibi cihazlar
 */
@Entity
@Table(name = "equipment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Integer equipmentId;
    
    @NotBlank(message = "Ekipman adı boş olamaz")
    @Size(max = 100, message = "Ekipman adı en fazla 100 karakter olabilir")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 50, message = "Tip en fazla 50 karakter olabilir")
    @Column(length = 50)
    private String type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'AVAILABLE'")
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;
    
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
}
