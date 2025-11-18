package com.kutuphanerezervasyon.kutuphane.config;

import com.kutuphanerezervasyon.kutuphane.entity.*;
import com.kutuphanerezervasyon.kutuphane.enums.*;
import com.kutuphanerezervasyon.kutuphane.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Uygulama başlangıcında örnek verileri yükleyen sınıf
 * Test ve demo amaçlı kullanıcı, oda, ekipman ve zaman dilimi verileri oluşturur
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Root Admin hesabı yoksa oluştur (her zaman kontrol et)
        if (!userRepository.existsByEmail("root@kutuphane.com")) {
            User rootAdmin = User.builder()
                    .name("Root Admin")
                    .email("root@kutuphane.com")
                    .password(passwordEncoder.encode("root123"))
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.save(rootAdmin);
            System.out.println("✅ Root Admin hesabı oluşturuldu: root@kutuphane.com / root123");
        }
        
        // Eğer diğer veriler zaten varsa örnek verileri ekleme
        if (userRepository.count() > 1) {
            System.out.println("⚠️ Veritabanında zaten veri mevcut, örnek veriler eklenmedi.");
            return;
        }

        User student1 = User.builder()
                .name("Ahmet Yılmaz")
                .email("ahmet@student.com")
                .password(passwordEncoder.encode("123456"))
                .role(UserRole.USER)
                .build();
        userRepository.save(student1);

        User student2 = User.builder()
                .name("Ayşe Demir")
                .email("ayse@student.com")
                .password(passwordEncoder.encode("123456"))
                .role(UserRole.USER)
                .build();
        userRepository.save(student2);

        User staff = User.builder()
                .name("Mehmet Kaya")
                .email("mehmet@staff.com")
                .password(passwordEncoder.encode("123456"))
                .role(UserRole.USER)
                .build();
        userRepository.save(staff);

        // Odalar oluştur
        Room room1 = Room.builder()
                .name("Çalışma Odası A1")
                .capacity(4)
                .status(RoomStatus.EMPTY)
                .build();
        roomRepository.save(room1);

        Room room2 = Room.builder()
                .name("Çalışma Odası A2")
                .capacity(6)
                .status(RoomStatus.EMPTY)
                .build();
        roomRepository.save(room2);

        Room room3 = Room.builder()
                .name("Çalışma Odası B1")
                .capacity(2)
                .status(RoomStatus.EMPTY)
                .build();
        roomRepository.save(room3);

        Room room4 = Room.builder()
                .name("Toplantı Odası")
                .capacity(10)
                .status(RoomStatus.MAINTENANCE)
                .build();
        roomRepository.save(room4);

        // Ekipmanlar oluştur
        Equipment laptop1 = Equipment.builder()
                .name("Dell Laptop #001")
                .type("Laptop")
                .status(EquipmentStatus.AVAILABLE)
                .build();
        equipmentRepository.save(laptop1);

        Equipment laptop2 = Equipment.builder()
                .name("HP Laptop #002")
                .type("Laptop")
                .status(EquipmentStatus.AVAILABLE)
                .build();
        equipmentRepository.save(laptop2);

        Equipment projector1 = Equipment.builder()
                .name("Projeksiyon Cihazı #001")
                .type("Projeksiyon")
                .status(EquipmentStatus.AVAILABLE)
                .build();
        equipmentRepository.save(projector1);

        Equipment tablet1 = Equipment.builder()
                .name("iPad Pro #001")
                .type("Tablet")
                .status(EquipmentStatus.RESERVED)
                .build();
        equipmentRepository.save(tablet1);

        // Zaman dilimleri oluştur
        TimeSlot slot1 = TimeSlot.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build();
        timeSlotRepository.save(slot1);

        TimeSlot slot2 = TimeSlot.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 30))
                .build();
        timeSlotRepository.save(slot2);

        TimeSlot slot3 = TimeSlot.builder()
                .startTime(LocalTime.of(11, 30))
                .endTime(LocalTime.of(13, 0))
                .build();
        timeSlotRepository.save(slot3);

        TimeSlot slot4 = TimeSlot.builder()
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(14, 30))
                .build();
        timeSlotRepository.save(slot4);

        TimeSlot slot5 = TimeSlot.builder()
                .startTime(LocalTime.of(14, 30))
                .endTime(LocalTime.of(16, 0))
                .build();
        timeSlotRepository.save(slot5);

        TimeSlot slot6 = TimeSlot.builder()
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(17, 30))
                .build();
        timeSlotRepository.save(slot6);

        // Örnek rezervasyonlar oluştur
        Reservation reservation1 = Reservation.builder()
                .user(student1)
                .room(room1)
                .timeSlot(slot2)
                .reservationDate(LocalDate.now().plusDays(1))
                .status(ReservationStatus.ONAYLANDI)
                .build();
        reservationRepository.save(reservation1);

        Reservation reservation2 = Reservation.builder()
                .user(student2)
                .equipment(laptop1)
                .timeSlot(slot3)
                .reservationDate(LocalDate.now().plusDays(1))
                .status(ReservationStatus.BEKLENIYOR)
                .build();
        reservationRepository.save(reservation2);

        Reservation reservation3 = Reservation.builder()
                .user(staff)
                .room(room2)
                .timeSlot(slot4)
                .reservationDate(LocalDate.now())
                .status(ReservationStatus.ONAYLANDI)
                .build();
        reservationRepository.save(reservation3);

        System.out.println("✅ Örnek veriler başarıyla yüklendi!");
        System.out.println("👤 Root Admin: root@kutuphane.com / root123");
        System.out.println("🎓 Öğrenci: ahmet@student.com / 123456");
        System.out.println("🌐 PostgreSQL: localhost:5432/kutuphane_db");
        System.out.println("   Username: postgres");
        System.out.println("   Password: postgres");
    }
}
