package com.logic.springjwt.services;

import com.logic.springjwt.DTO.UpdateStaffDetailsDTO;
import com.logic.springjwt.DTO.UpdateStaffUnitDTO;
import com.logic.springjwt.DTO.UpdateStatusDTO;
import com.logic.springjwt.models.StaffDetails;
import com.logic.springjwt.models.StaffUnits;
import com.logic.springjwt.projection.StaffUnitNameProjection;
import com.logic.springjwt.repository.StaffDetailsRepository;
import com.logic.springjwt.repository.StaffUnitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffService {
    private final StaffUnitsRepository staffUnitsRepository;
    private final StaffDetailsRepository staffDetailsRepository;

    @Autowired
    public StaffService(StaffUnitsRepository staffUnitsRepository, StaffDetailsRepository staffDetailsRepository) {
        this.staffUnitsRepository = staffUnitsRepository;
        this.staffDetailsRepository = staffDetailsRepository;
    }

    @Transactional
    public StaffUnits saveStaffUnit(StaffUnits staffUnits) {
        staffUnits.setCreatedAt(LocalDateTime.now());
        staffUnits.setLatestUpdate(LocalDateTime.now());
        return staffUnitsRepository.save(staffUnits);
    }

    @Transactional
    private StaffDetails saveStaffDetails(StaffDetails staffDetails) {
        return staffDetailsRepository.save(staffDetails);
    }

    private StaffUnits getStaffUnitByUId(Long uId) {
        return staffUnitsRepository.findById(uId)
                .orElseThrow(() -> new RuntimeException("Staff unit not found by " + uId));
    }

    @Transactional
    public StaffDetails saveStaffToUnit(Long unitId, StaffDetails staffDetails) {
        StaffUnits tempStaffUnit = getStaffUnitByUId(unitId);
        tempStaffUnit.setLatestUpdate(LocalDateTime.now());
        staffDetails.setAddedAt(LocalDateTime.now());
        staffDetails.setLatestUpdate(LocalDateTime.now());
        staffDetails.setStaffUnits(tempStaffUnit);
        return saveStaffDetails(staffDetails);
    }

    public List<StaffUnits> getAllStaff() {
        return staffUnitsRepository.findAll();
    }

    public List<StaffUnitNameProjection> getAllUnitNames() {
        return staffUnitsRepository.findAllBy();
    }

    public List<StaffDetails> getAllStaffDetails() {
        return staffDetailsRepository.findAllByOrderByAddedAtDesc();
    }

    public List<StaffDetails> getDetailsByUnitId(Long unitId) {
        StaffUnits tempStaffUnit = getStaffUnitByUId(unitId);
        return tempStaffUnit.getStaffDetails();
    }

    public StaffUnits getUnitByUnitId(Long unitId) {
        return staffUnitsRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not find with unit id: " + unitId));
    }

    public List<StaffDetails> searchByStaffName(String query) {
        return staffDetailsRepository.findByNameContainingIgnoreCase(query);
    }

    @Transactional
    public StaffUnits updateUnitStatus(Long unitId, UpdateStatusDTO updateStatusDTO) {
        StaffUnits tempUnit = getStaffUnitByUId(unitId);

        updateStatusDTO.setUpdateAt(LocalDateTime.now());

        tempUnit.setStatus(updateStatusDTO.getStatus());
        tempUnit.setLatestUpdate(updateStatusDTO.getUpdateAt());
        return staffUnitsRepository.save(tempUnit);
    }

    public StaffUnits getUnitsByDetailsId(Long detailsId) {
        return staffDetailsRepository.findUnitsByDetailsId(detailsId);
    }

    public StaffDetails getDetailsByDetailsId(Long detailsId) {
        return staffDetailsRepository.findById(detailsId)
                .orElseThrow(() -> new RuntimeException("Details not found with id: " + detailsId));
    }

    @Transactional
    public StaffDetails updateDetailsStatus(Long detailsId, UpdateStatusDTO updateStatusDTO) {
        StaffDetails tempDetails = getDetailsByDetailsId(detailsId);

        updateStatusDTO.setUpdateAt(LocalDateTime.now());

        StaffUnits tempUnit = getUnitsByDetailsId(detailsId);
        tempUnit.setLatestUpdate(updateStatusDTO.getUpdateAt());

        tempDetails.setStatus(updateStatusDTO.getStatus());
        tempDetails.setLatestUpdate(updateStatusDTO.getUpdateAt());
        return staffDetailsRepository.save(tempDetails);
    }

    @Transactional
    public StaffUnits updateUnitByUnitId(Long unitId, UpdateStaffUnitDTO unitDTO) {
        StaffUnits tempUnit = getStaffUnitByUId(unitId);

        unitDTO.setLatestUpdate(LocalDateTime.now());
        tempUnit.setStaffUnit(unitDTO.getStaffUnit());
        tempUnit.setLatestUpdate(unitDTO.getLatestUpdate());
        return staffUnitsRepository.save(tempUnit);
    }

    @Transactional
    public StaffDetails updateDetailsByDetailsId(Long detailsId, UpdateStaffDetailsDTO staffDetailsDTO) {
        StaffDetails tempDetails = getDetailsByDetailsId(detailsId);

        staffDetailsDTO.setLastUpdateAt(LocalDateTime.now());

        StaffUnits beforeEdit = getUnitsByDetailsId(detailsId);
        beforeEdit.setLatestUpdate(staffDetailsDTO.getLastUpdateAt());

        StaffUnits tempUnit = getStaffUnitByUId(staffDetailsDTO.getUpdatedUnitId());
        tempUnit.setLatestUpdate(staffDetailsDTO.getLastUpdateAt());
        tempDetails.setStaffUnits(tempUnit);
        tempDetails.setDesignation(staffDetailsDTO.getDesignation());
        tempDetails.setEmail(staffDetailsDTO.getEmail());
        tempDetails.setContactNumber(staffDetailsDTO.getContactNumber());
        tempDetails.setLinkedIn(staffDetailsDTO.getLinkedIn());
        tempDetails.setLatestUpdate(staffDetailsDTO.getLastUpdateAt());
        return staffDetailsRepository.save(tempDetails);
    }

    @Transactional
    public void deleteStaffUnitByUnitId(Long unitId) {
        staffUnitsRepository.deleteById(unitId);
    }

    @Transactional
    public void deleteDetailsByDetailsId(Long detailsId) {
        StaffUnits tempUnit = getUnitsByDetailsId(detailsId);
        tempUnit.setLatestUpdate(LocalDateTime.now());
        staffDetailsRepository.deleteById(detailsId);
    }
}
