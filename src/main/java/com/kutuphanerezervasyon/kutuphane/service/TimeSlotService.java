package com.kutuphanerezervasyon.kutuphane.service;

import com.kutuphanerezervasyon.kutuphane.dto.TimeSlotDTO;
import com.kutuphanerezervasyon.kutuphane.entity.TimeSlot;
import com.kutuphanerezervasyon.kutuphane.exception.InvalidOperationException;
import com.kutuphanerezervasyon.kutuphane.exception.ResourceNotFoundException;
import com.kutuphanerezervasyon.kutuphane.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 Zaman dilimi yönetimi servis katmanı
 Rezervasyon için kullanılabilecek sabit zaman dilimlerini yönetir
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public TimeSlotDTO createTimeSlot(TimeSlotDTO timeSlotDTO) {
        // Zaman kontrolü
        if (timeSlotDTO.getStartTime() == null || timeSlotDTO.getEndTime() == null) {
            throw new InvalidOperationException("Başlangıç ve bitiş saati belirtilmelidir");
        }
        
        if (timeSlotDTO.getStartTime().isAfter(timeSlotDTO.getEndTime()) || 
            timeSlotDTO.getStartTime().equals(timeSlotDTO.getEndTime())) {
            throw new InvalidOperationException("Başlangıç saati bitiş saatinden önce olmalıdır");
        }

        // Aynı zaman dilimi var mı kontrol et
        if (timeSlotRepository.findByStartTimeAndEndTime(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime()).isPresent()) {
            throw new InvalidOperationException("Bu zaman dilimi zaten mevcut");
        }

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStartTime(timeSlotDTO.getStartTime());
        timeSlot.setEndTime(timeSlotDTO.getEndTime());

        TimeSlot savedTimeSlot = timeSlotRepository.save(timeSlot);
        return convertToDTO(savedTimeSlot);
    }

    @Transactional(readOnly = true)
    public TimeSlotDTO getTimeSlotById(Integer timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Zaman dilimi bulunamadı: " + timeSlotId));
        return convertToDTO(timeSlot);
    }

    @Transactional(readOnly = true)
    public List<TimeSlotDTO> getAllTimeSlots() {
        return timeSlotRepository.findAllOrderByStartTime().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimeSlotDTO> getTimeSlotsInRange(LocalTime startTime, LocalTime endTime) {
        return timeSlotRepository.findTimeSlotsInRange(startTime, endTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TimeSlotDTO updateTimeSlot(Integer timeSlotId, TimeSlotDTO timeSlotDTO) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Zaman dilimi bulunamadı: " + timeSlotId));

        if (timeSlotDTO.getStartTime() != null && timeSlotDTO.getEndTime() != null) {
            if (timeSlotDTO.getStartTime().isAfter(timeSlotDTO.getEndTime()) || 
                timeSlotDTO.getStartTime().equals(timeSlotDTO.getEndTime())) {
                throw new InvalidOperationException("Başlangıç saati bitiş saatinden önce olmalıdır");
            }
            timeSlot.setStartTime(timeSlotDTO.getStartTime());
            timeSlot.setEndTime(timeSlotDTO.getEndTime());
        }

        TimeSlot updatedTimeSlot = timeSlotRepository.save(timeSlot);
        return convertToDTO(updatedTimeSlot);
    }

    public void deleteTimeSlot(Integer timeSlotId) {
        if (!timeSlotRepository.existsById(timeSlotId)) {
            throw new ResourceNotFoundException("Zaman dilimi bulunamadı: " + timeSlotId);
        }
        timeSlotRepository.deleteById(timeSlotId);
    }

    private TimeSlotDTO convertToDTO(TimeSlot timeSlot) {
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setTimeSlotId(timeSlot.getTimeSlotId());
        dto.setStartTime(timeSlot.getStartTime());
        dto.setEndTime(timeSlot.getEndTime());
        return dto;
    }
}
