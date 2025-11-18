package com.kutuphanerezervasyon.kutuphane.service;

import com.kutuphanerezervasyon.kutuphane.dto.RoomDTO;
import com.kutuphanerezervasyon.kutuphane.entity.Room;
import com.kutuphanerezervasyon.kutuphane.enums.RoomStatus;
import com.kutuphanerezervasyon.kutuphane.exception.ResourceNotFoundException;
import com.kutuphanerezervasyon.kutuphane.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/*
  Oda yönetimi işlemlerini gerçekleştiren servis
 Oda ekleme, güncelleme, silme ve müsaitlik sorgulama işlemleri
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomDTO createRoom(RoomDTO roomDTO) {
        Room room = new Room();
        room.setName(roomDTO.getName());
        room.setCapacity(roomDTO.getCapacity());
        room.setStatus(roomDTO.getStatus() != null ? roomDTO.getStatus() : RoomStatus.EMPTY);

        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }

    @Transactional(readOnly = true)
    public RoomDTO getRoomById(Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Oda bulunamadı: " + roomId));
        return convertToDTO(room);
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByMinCapacity(Integer minCapacity) {
        return roomRepository.findByCapacityGreaterThanEqual(minCapacity).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> searchRoomsByName(String keyword) {
        return roomRepository.searchByName(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getAvailableRooms(LocalDate date, Integer timeSlotId) {
        return roomRepository.findAvailableRooms(date, timeSlotId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoomDTO updateRoom(Integer roomId, RoomDTO roomDTO) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Oda bulunamadı: " + roomId));

        if (roomDTO.getName() != null) {
            room.setName(roomDTO.getName());
        }
        if (roomDTO.getCapacity() != null) {
            room.setCapacity(roomDTO.getCapacity());
        }
        if (roomDTO.getStatus() != null) {
            room.setStatus(roomDTO.getStatus());
        }

        Room updatedRoom = roomRepository.save(room);
        return convertToDTO(updatedRoom);
    }

    public RoomDTO updateRoomStatus(Integer roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Oda bulunamadı: " + roomId));
        
        room.setStatus(status);
        Room updatedRoom = roomRepository.save(room);
        return convertToDTO(updatedRoom);
    }

    public void deleteRoom(Integer roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Oda bulunamadı: " + roomId);
        }
        roomRepository.deleteById(roomId);
    }

    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setRoomId(room.getRoomId());
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setStatus(room.getStatus());
        return dto;
    }
}
