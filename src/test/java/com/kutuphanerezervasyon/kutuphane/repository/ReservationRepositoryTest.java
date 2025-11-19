package com.kutuphanerezervasyon.kutuphane.repository;

import com.kutuphanerezervasyon.kutuphane.entity.*;
import com.kutuphanerezervasyon.kutuphane.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("ReservationRepository Test")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    private User user;
    private Room room;
    private Equipment equipment;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        // Test verileri 
        user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(UserRole.USER);
        user = userRepository.save(user);

        room = new Room();
        room.setName("Test Room");
        room.setCapacity(10);
        room.setStatus(RoomStatus.EMPTY);
        room = roomRepository.save(room);

        equipment = new Equipment();
        equipment.setName("Test Equipment");
        equipment.setType("Projector");
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment = equipmentRepository.save(equipment);

        timeSlot = new TimeSlot();
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(10, 0));
        timeSlot = timeSlotRepository.save(timeSlot);
    }

    @Test
    @DisplayName("Oda çakışması kontrolü - çakışma varsa 1 döner")
    void odaCakismasi_CakismaVarsa_1Doner() {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setTimeSlot(timeSlot);
        reservation.setReservationDate(LocalDate.now().plusDays(1));
        reservation.setStatus(ReservationStatus.ONAYLANDI);
        reservationRepository.save(reservation);

        Long conflict = reservationRepository.checkRoomConflict(
            room.getRoomId(),
            LocalDate.now().plusDays(1),
            timeSlot.getTimeSlotId()
        );

        assertEquals(1L, conflict);
    }

    @Test
    @DisplayName("Oda çakışması kontrolü - çakışma yoksa 0 döner")

    void odaCakismasi_CakismaYoksa_0Doner() {
        Long conflict = reservationRepository.checkRoomConflict(
            room.getRoomId(),
            LocalDate.now().plusDays(1),
            timeSlot.getTimeSlotId()
        );

        assertEquals(0L, conflict);
    }

    @Test
    @DisplayName("Kullanıcı zaman dilimi çakışması kontrolü")

    void kullaniciZamanDilimiCakismasi_KontrolEdilir() {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setTimeSlot(timeSlot);
        reservation.setReservationDate(LocalDate.now().plusDays(1));
        reservation.setStatus(ReservationStatus.ONAYLANDI);
        reservationRepository.save(reservation);

        Long conflict = reservationRepository.checkUserTimeSlotConflict(
            user.getUserId(),
            LocalDate.now().plusDays(1),
            timeSlot.getTimeSlotId()
        );

        assertEquals(1L, conflict);
    }

    @Test
    @DisplayName("Aktif rezervasyon sayısı kontrolü")

    void aktifRezervasyonSayisi_DogruHesaplanir() {
        Reservation res1 = new Reservation();
        res1.setUser(user);
        res1.setRoom(room);
        res1.setTimeSlot(timeSlot);
        res1.setReservationDate(LocalDate.now().plusDays(1));
        res1.setStatus(ReservationStatus.ONAYLANDI);
        reservationRepository.save(res1);

        Reservation res2 = new Reservation();
        res2.setUser(user);
        res2.setRoom(room);
        res2.setTimeSlot(timeSlot);
        res2.setReservationDate(LocalDate.now().plusDays(2));
        res2.setStatus(ReservationStatus.BEKLENIYOR);
        reservationRepository.save(res2);

        Reservation res3 = new Reservation();
        res3.setUser(user);
        res3.setRoom(room);
        res3.setTimeSlot(timeSlot);
        res3.setReservationDate(LocalDate.now().plusDays(3));
        res3.setStatus(ReservationStatus.IPTAL_EDILDI);
        reservationRepository.save(res3);

        Long count = reservationRepository.countActiveReservationsByUserId(
            user.getUserId(),
            LocalDate.now(),
            LocalTime.now()
        );

        assertEquals(2L, count);
    }

    @Test
    @DisplayName("Ekipman çakışması kontrolü")

    void ekipmanCakismasi_KontrolEdilir() {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEquipment(equipment);
        reservation.setTimeSlot(timeSlot);
        reservation.setReservationDate(LocalDate.now().plusDays(1));
        reservation.setStatus(ReservationStatus.ONAYLANDI);
        reservationRepository.save(reservation);

        Long conflict = reservationRepository.checkEquipmentConflict(
            equipment.getEquipmentId(),
            LocalDate.now().plusDays(1),
            timeSlot.getTimeSlotId()
        );

        assertEquals(1L, conflict);
    }
}
