package com.kutuphanerezervasyon.kutuphane.service;

import com.kutuphanerezervasyon.kutuphane.dto.EquipmentDTO;
import com.kutuphanerezervasyon.kutuphane.entity.Equipment;
import com.kutuphanerezervasyon.kutuphane.enums.EquipmentStatus;
import com.kutuphanerezervasyon.kutuphane.exception.ResourceNotFoundException;
import com.kutuphanerezervasyon.kutuphane.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/*
 Ekipman yönetimi işlemlerini gerçekleştiren servis
 Ekipman ekleme, güncelleme, silme ve müsaitlik sorgulama
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentDTO createEquipment(EquipmentDTO equipmentDTO) {
        Equipment equipment = new Equipment();
        equipment.setName(equipmentDTO.getName());
        equipment.setType(equipmentDTO.getType());
        equipment.setStatus(equipmentDTO.getStatus() != null ? equipmentDTO.getStatus() : EquipmentStatus.AVAILABLE);

        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(savedEquipment);
    }

    @Transactional(readOnly = true)
    public EquipmentDTO getEquipmentById(Integer equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ekipman bulunamadı: " + equipmentId));
        return convertToDTO(equipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentDTO> getAllEquipment() {
        return equipmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipmentDTO> getEquipmentByStatus(EquipmentStatus status) {
        return equipmentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipmentDTO> getEquipmentByType(String type) {
        return equipmentRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipmentDTO> searchEquipment(String keyword) {
        return equipmentRepository.searchEquipment(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipmentDTO> getAvailableEquipment(LocalDate date, Integer timeSlotId) {
        return equipmentRepository.findAvailableEquipment(date, timeSlotId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EquipmentDTO updateEquipment(Integer equipmentId, EquipmentDTO equipmentDTO) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ekipman bulunamadı: " + equipmentId));

        if (equipmentDTO.getName() != null) {
            equipment.setName(equipmentDTO.getName());
        }
        if (equipmentDTO.getType() != null) {
            equipment.setType(equipmentDTO.getType());
        }
        if (equipmentDTO.getStatus() != null) {
            equipment.setStatus(equipmentDTO.getStatus());
        }

        Equipment updatedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(updatedEquipment);
    }

    public EquipmentDTO updateEquipmentStatus(Integer equipmentId, EquipmentStatus status) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ekipman bulunamadı: " + equipmentId));
        
        equipment.setStatus(status);
        Equipment updatedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(updatedEquipment);
    }

    public void deleteEquipment(Integer equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new ResourceNotFoundException("Ekipman bulunamadı: " + equipmentId);
        }
        equipmentRepository.deleteById(equipmentId);
    }

    private EquipmentDTO convertToDTO(Equipment equipment) {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setEquipmentId(equipment.getEquipmentId());
        dto.setName(equipment.getName());
        dto.setType(equipment.getType());
        dto.setStatus(equipment.getStatus());
        return dto;
    }
}
