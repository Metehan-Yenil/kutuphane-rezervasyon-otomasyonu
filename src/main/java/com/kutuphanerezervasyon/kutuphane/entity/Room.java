package com.kutuphanerezervasyon.kutuphane.entity;

import com.kutuphanerezervasyon.kutuphane.enums.RoomStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Kütüphanedeki çalışma odalarını temsil eden entity
 * Oda adı, kapasitesi ve durumu (boş, dolu, bakımda) saklanır
 */
@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;
    
    @NotBlank(message = "Oda adı boş olamaz")
    @Size(max = 100, message = "Oda adı en fazla 100 karakter olabilir")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "Kapasite boş olamaz")
    @Positive(message = "Kapasite pozitif bir sayı olmalıdır")
    @Column(nullable = false)
    private Integer capacity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'EMPTY'")
    private RoomStatus status = RoomStatus.EMPTY;
    
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
}
