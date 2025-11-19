package com.kutuphanerezervasyon.kutuphane.service;

import com.kutuphanerezervasyon.kutuphane.dto.ReservationDTO;
import com.kutuphanerezervasyon.kutuphane.dto.ReservationRequest;
import com.kutuphanerezervasyon.kutuphane.entity.*;
import com.kutuphanerezervasyon.kutuphane.enums.*;
import com.kutuphanerezervasyon.kutuphane.exception.MaxReservationLimitException;
import com.kutuphanerezervasyon.kutuphane.exception.ReservationConflictException;
import com.kutuphanerezervasyon.kutuphane.repository.*;
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
@DisplayName("ReservationService Integration Test")
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User testUser;
    private Room testRoom;
    private Equipment testEquipment;
    private TimeSlot testTimeSlot;

    @BeforeEach
    void setUp() {
        // Test kullanıcısı oluştur
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.USER);
        testUser = userRepository.save(testUser);

        // Test odası oluştur
        testRoom = new Room();
        testRoom.setName("Test Room");
        testRoom.setCapacity(10);
        testRoom.setStatus(RoomStatus.EMPTY);
        testRoom = roomRepository.save(testRoom);

        // Test ekipmanı oluştur
        testEquipment = new Equipment();
        testEquipment.setName("Test Equipment");
        testEquipment.setType("Projector");
        testEquipment.setStatus(EquipmentStatus.AVAILABLE);
        testEquipment = equipmentRepository.save(testEquipment);

        // Test zaman dilimi oluştur
        testTimeSlot = new TimeSlot();
        testTimeSlot.setStartTime(LocalTime.of(9, 0));
        testTimeSlot.setEndTime(LocalTime.of(10, 0));
        testTimeSlot = timeSlotRepository.save(testTimeSlot);
    }

    @Test
    @DisplayName("Rezervasyon başarıyla oluşturulmalı")
    void rezervasyonOlustur_Basarili() {
        ReservationRequest request = new ReservationRequest();
        request.setUserId(testUser.getUserId());
        request.setRoomId(testRoom.getRoomId());
        request.setEquipmentId(testEquipment.getEquipmentId());
        request.setTimeSlotId(testTimeSlot.getTimeSlotId());
        request.setReservationDate(LocalDate.now().plusDays(1));

        ReservationDTO result = reservationService.createReservation(request);

        assertNotNull(result);
        assertNotNull(result.getUser());
        assertEquals(testUser.getUserId(), result.getUser().getUserId());
        assertEquals(ReservationStatus.BEKLENIYOR, result.getStatus());
    }

    @Test
    @DisplayName("Bakımdaki odaya rezervasyon yapılamamalı")
    void bakimdakiOdaya_RezervasyonYapilamaz() {
        testRoom.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(testRoom);

        ReservationRequest request = new ReservationRequest();
        request.setUserId(testUser.getUserId());
        request.setRoomId(testRoom.getRoomId());
        request.setTimeSlotId(testTimeSlot.getTimeSlotId());
        request.setReservationDate(LocalDate.now().plusDays(1));

        assertThrows(Exception.class, () -> reservationService.createReservation(request));
    }

    @Test
    @DisplayName("Aynı kullanıcı aynı zaman diliminde iki rezervasyon yapamamalı")
    void ayniKullanici_AyniZamanDilimi_IkiRezervasyonYapilamaz() {
        ReservationRequest request1 = new ReservationRequest();
        request1.setUserId(testUser.getUserId());
        request1.setRoomId(testRoom.getRoomId());
        request1.setTimeSlotId(testTimeSlot.getTimeSlotId());
        request1.setReservationDate(LocalDate.now().plusDays(1));
        reservationService.createReservation(request1);

        Room room2 = new Room();
        room2.setName("Test Room 2");
        room2.setCapacity(10);
        room2.setStatus(RoomStatus.EMPTY);
        room2 = roomRepository.save(room2);

        ReservationRequest request2 = new ReservationRequest();
        request2.setUserId(testUser.getUserId());
        request2.setRoomId(room2.getRoomId());
        request2.setTimeSlotId(testTimeSlot.getTimeSlotId());
        request2.setReservationDate(LocalDate.now().plusDays(1));

        assertThrows(ReservationConflictException.class, 
            () -> reservationService.createReservation(request2));
    }

    @Test
    @DisplayName("Maksimum 2 aktif rezervasyon kontrolü")
    void maksimumIkiAktifRezervasyon_KontrolEdilir() {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        TimeSlot slot2 = new TimeSlot();
        slot2.setStartTime(LocalTime.of(10, 0));
        slot2.setEndTime(LocalTime.of(11, 0));
        slot2 = timeSlotRepository.save(slot2);

        TimeSlot slot3 = new TimeSlot();
        slot3.setStartTime(LocalTime.of(11, 0));
        slot3.setEndTime(LocalTime.of(12, 0));
        slot3 = timeSlotRepository.save(slot3);

        ReservationRequest request1 = new ReservationRequest();
        request1.setUserId(testUser.getUserId());
        request1.setRoomId(testRoom.getRoomId());
        request1.setTimeSlotId(testTimeSlot.getTimeSlotId());
        request1.setReservationDate(futureDate);
        reservationService.createReservation(request1);

        ReservationRequest request2 = new ReservationRequest();
        request2.setUserId(testUser.getUserId());
        request2.setRoomId(testRoom.getRoomId());
        request2.setTimeSlotId(slot2.getTimeSlotId());
        request2.setReservationDate(futureDate);
        reservationService.createReservation(request2);

        ReservationRequest request3 = new ReservationRequest();
        request3.setUserId(testUser.getUserId());
        request3.setRoomId(testRoom.getRoomId());
        request3.setTimeSlotId(slot3.getTimeSlotId());
        request3.setReservationDate(futureDate);

        assertThrows(MaxReservationLimitException.class,
            () -> reservationService.createReservation(request3));
    }
}
