package com.kutuphanerezervasyon.kutuphane.service;

import com.kutuphanerezervasyon.kutuphane.dto.*;
import com.kutuphanerezervasyon.kutuphane.entity.*;
import com.kutuphanerezervasyon.kutuphane.enums.EquipmentStatus;
import com.kutuphanerezervasyon.kutuphane.enums.ReservationStatus;
import com.kutuphanerezervasyon.kutuphane.enums.RoomStatus;
import com.kutuphanerezervasyon.kutuphane.exception.InvalidOperationException;
import com.kutuphanerezervasyon.kutuphane.exception.MaxReservationLimitException;
import com.kutuphanerezervasyon.kutuphane.exception.ReservationConflictException;
import com.kutuphanerezervasyon.kutuphane.exception.ResourceNotFoundException;
import com.kutuphanerezervasyon.kutuphane.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 Rezervasyon işlemlerini yöneten ana servis katmanı
 Çakışma kontrolü, limit kontrolü, onaylama ve iptal işlemleri
 Kullanıcı başına maksimum 2 aktif rezervasyon sınırlaması burada 
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Value("${app.reservation.max-active-per-user:2}")
    private int maxActiveReservationsPerUser;

    @Value("${app.reservation.min-duration-minutes:30}")
    private int minDurationMinutes;

    @Value("${app.reservation.max-duration-hours:3}")
    private int maxDurationHours;

    @Value("${app.reservation.min-cancellation-hours:1}")
    private int minCancellationHours;

    public ReservationDTO createReservation(ReservationRequest request) {
        System.out.println("=== REZERVASYON OLUŞTURMA BAŞLADI ===");
        System.out.println("Request: " + request);
        System.out.println("UserId: " + request.getUserId());
        System.out.println("RoomId: " + request.getRoomId());
        System.out.println("EquipmentId: " + request.getEquipmentId());
        System.out.println("TimeSlotId: " + request.getTimeSlotId());
        System.out.println("ReservationDate: " + request.getReservationDate());
        
        // Kullanıcı kontrolü
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + request.getUserId()));

        // TimeSlot kontrolü
        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Zaman dilimi bulunamadı: " + request.getTimeSlotId()));

        // En az bir kaynak (Room veya Equipment) olmalı
        if (request.getRoomId() == null && request.getEquipmentId() == null) {
            throw new InvalidOperationException("En az bir oda veya ekipman seçilmelidir");
        }

        Room room = null;
        Equipment equipment = null;

        // Room kontrolü
        if (request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Oda bulunamadı: " + request.getRoomId()));
            
            // Oda durumu kontrolü
            if (room.getStatus() == RoomStatus.MAINTENANCE) {
                throw new InvalidOperationException("Bu oda şu anda bakımda, rezervasyon yapılamaz");
            }
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                throw new InvalidOperationException("Bu oda şu anda dolu, rezervasyon yapılamaz");
            }
            
            // Oda çakışma kontrolü
            Long roomConflict = reservationRepository.checkRoomConflict(
                    request.getRoomId(), request.getReservationDate(), request.getTimeSlotId());
            if (roomConflict > 0) {
                throw new ReservationConflictException("Bu oda seçilen tarih ve saatte zaten rezerve edilmiş");
            }
        }

        // Equipment kontrolü
        if (request.getEquipmentId() != null) {
            equipment = equipmentRepository.findById(request.getEquipmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ekipman bulunamadı: " + request.getEquipmentId()));
            
            // Ekipman durumu kontrolü
            if (equipment.getStatus() == EquipmentStatus.MAINTENANCE) {
                throw new InvalidOperationException("Bu ekipman şu anda bakımda, rezervasyon yapılamaz");
            }
            if (equipment.getStatus() == EquipmentStatus.RESERVED) {
                throw new InvalidOperationException("Bu ekipman şu anda rezerve edilmiş, rezervasyon yapılamaz");
            }
            
            // Ekipman çakışma kontrolü
            Long equipmentConflict = reservationRepository.checkEquipmentConflict(
                    request.getEquipmentId(), request.getReservationDate(), request.getTimeSlotId());
            if (equipmentConflict > 0) {
                throw new ReservationConflictException("Bu ekipman seçilen tarih ve saatte zaten rezerve edilmiş");
            }
        }

        // Tarih kontrolü - geçmiş tarih kontrolü
        if (request.getReservationDate().isBefore(LocalDate.now())) {
            throw new InvalidOperationException("Geçmiş tarih için rezervasyon yapılamaz");
        }

        // Kullanıcının bu tarih ve saatte başka rezervasyonu var mı kontrolü
        Long userTimeSlotConflict = reservationRepository.checkUserTimeSlotConflict(
                request.getUserId(), request.getReservationDate(), request.getTimeSlotId());
        if (userTimeSlotConflict > 0) {
            throw new ReservationConflictException("Bu tarih ve saatte zaten bir rezervasyonunuz var");
        }

        // Kullanıcının aktif rezervasyon sayısı kontrolü
        Long activeReservationCount = reservationRepository.countActiveReservationsByUserId(
                request.getUserId(), LocalDate.now(), LocalTime.now());
        if (activeReservationCount >= maxActiveReservationsPerUser) {
            throw new MaxReservationLimitException(
                    "Maksimum " + maxActiveReservationsPerUser + " aktif rezervasyon yapabilirsiniz");
        }

        // Süre kontrolü
        Duration duration = Duration.between(timeSlot.getStartTime(), timeSlot.getEndTime());
        if (duration.toMinutes() < minDurationMinutes) {
            throw new InvalidOperationException("Rezervasyon süresi en az " + minDurationMinutes + " dakika olmalıdır");
        }
        if (duration.toHours() > maxDurationHours) {
            throw new InvalidOperationException("Rezervasyon süresi en fazla " + maxDurationHours + " saat olabilir");
        }

        // Rezervasyon oluştur
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setEquipment(equipment);
        reservation.setTimeSlot(timeSlot);
        reservation.setReservationDate(request.getReservationDate());
        reservation.setStatus(ReservationStatus.BEKLENIYOR);

        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDTO(savedReservation);
    }

    @Transactional(readOnly = true)
    public ReservationDTO getReservationById(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervasyon bulunamadı: " + reservationId));
        return convertToDTO(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId);
        }
        return reservationRepository.findByUserUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getActiveReservationsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId);
        }
        return reservationRepository.findActiveReservationsByUserId(userId, LocalDate.now(), LocalTime.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByReservationDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getPendingReservations() {
        return reservationRepository.findPendingReservations().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO confirmReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervasyon bulunamadı: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.BEKLENIYOR) {
            throw new InvalidOperationException("Sadece bekleyen rezervasyonlar onaylanabilir");
        }

        reservation.confirm();
        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToDTO(updatedReservation);
    }

    public ReservationDTO cancelReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervasyon bulunamadı: " + reservationId));

        if (reservation.getStatus() == ReservationStatus.IPTAL_EDILDI) {
            throw new InvalidOperationException("Bu rezervasyon zaten iptal edilmiş");
        }

        // İptal süresi kontrolü
        LocalDateTime reservationDateTime = LocalDateTime.of(
                reservation.getReservationDate(),
                reservation.getTimeSlot().getStartTime()
        );
        LocalDateTime now = LocalDateTime.now();
        
        if (Duration.between(now, reservationDateTime).toHours() < minCancellationHours) {
            throw new InvalidOperationException(
                    "Rezervasyon başlangıç saatinden en az " + minCancellationHours + " saat önce iptal edilmelidir");
        }

        reservation.cancel();
        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToDTO(updatedReservation);
    }

    // Admin için - zaman kontrolü olmadan iptal
    public ReservationDTO adminCancelReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervasyon bulunamadı: " + reservationId));

        if (reservation.getStatus() == ReservationStatus.IPTAL_EDILDI) {
            throw new InvalidOperationException("Bu rezervasyon zaten iptal edilmiş");
        }

        reservation.cancel();
        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToDTO(updatedReservation);
    }

    public void deleteReservation(Integer reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new ResourceNotFoundException("Rezervasyon bulunamadı: " + reservationId);
        }
        reservationRepository.deleteById(reservationId);
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setReservationId(reservation.getReservationId());
        
        // User
        if (reservation.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(reservation.getUser().getUserId());
            userDTO.setName(reservation.getUser().getName());
            userDTO.setEmail(reservation.getUser().getEmail());
            userDTO.setRole(reservation.getUser().getRole());
            dto.setUser(userDTO);
        }
        
        // Room
        if (reservation.getRoom() != null) {
            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setRoomId(reservation.getRoom().getRoomId());
            roomDTO.setName(reservation.getRoom().getName());
            roomDTO.setCapacity(reservation.getRoom().getCapacity());
            roomDTO.setStatus(reservation.getRoom().getStatus());
            dto.setRoom(roomDTO);
        }
        
        // Equipment
        if (reservation.getEquipment() != null) {
            EquipmentDTO equipmentDTO = new EquipmentDTO();
            equipmentDTO.setEquipmentId(reservation.getEquipment().getEquipmentId());
            equipmentDTO.setName(reservation.getEquipment().getName());
            equipmentDTO.setType(reservation.getEquipment().getType());
            equipmentDTO.setStatus(reservation.getEquipment().getStatus());
            dto.setEquipment(equipmentDTO);
        }
        
        // TimeSlot
        if (reservation.getTimeSlot() != null) {
            TimeSlotDTO timeSlotDTO = new TimeSlotDTO();
            timeSlotDTO.setTimeSlotId(reservation.getTimeSlot().getTimeSlotId());
            timeSlotDTO.setStartTime(reservation.getTimeSlot().getStartTime());
            timeSlotDTO.setEndTime(reservation.getTimeSlot().getEndTime());
            dto.setTimeSlot(timeSlotDTO);
        }
        
        dto.setReservationDate(reservation.getReservationDate());
        dto.setStatus(reservation.getStatus());
        
        return dto;
    }
}
