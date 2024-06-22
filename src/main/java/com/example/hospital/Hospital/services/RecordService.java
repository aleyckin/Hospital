package com.example.hospital.Hospital.services;

import com.example.hospital.Hospital.controllers.RecordDTO;
import com.example.hospital.Hospital.models.Record;
import com.example.hospital.Hospital.models.enums.Place;
import com.example.hospital.Hospital.models.enums.Status;
import com.example.hospital.Hospital.repository.RecordRepository;
import com.example.hospital.Util.Validation.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RecordService {
    private final RecordRepository recordRepository;
    private final ValidatorUtil validatorUtil;
    private final DoctorService doctorService;
    private final UserService userService;

    @Autowired
    public RecordService(RecordRepository recordRepository, ValidatorUtil validatorUtil, DoctorService doctorService, UserService userService) {
        this.recordRepository = recordRepository;
        this.validatorUtil = validatorUtil;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @Transactional
    public Record addRecord(RecordDTO recordDTO) throws IOException {
        final Record record = new Record(recordDTO);
        record.setDoctor(doctorService.findDoctor(recordDTO.getDoctor_id()));
        record.setUser(userService.findUser(recordDTO.getUser_id()));
        validatorUtil.validate(record);
        return recordRepository.save(record);
    }

    @Transactional
    public Record addRecord(Double price, Place place, Status status) {
        final Record record = new Record(price, place, status);
        validatorUtil.validate(record);
        return recordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public Record findRecord(Long id) {
        final Optional<Record> post = recordRepository.findById(id);
        return post.orElseThrow(() -> new RecordNotFoundException(id));
    }

    @Transactional
    public void saveRecord(Record record) {
        recordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<Record> findAllRecords() {
        return recordRepository.findAll();
    }

    @Transactional
    public Record updateRecord(Long id, RecordDTO recordDTO) {
        final Record currentRecord = findRecord(id);
        currentRecord.setPrice(recordDTO.getPrice());
        currentRecord.setPlace(recordDTO.getPlace());
        currentRecord.setStatus(recordDTO.getStatus());
        currentRecord.setDoctor(doctorService.findDoctor(recordDTO.getDoctor_id()));
        currentRecord.setUser(userService.findUser(recordDTO.getUser_id()));
        validatorUtil.validate(currentRecord);
        return recordRepository.save(currentRecord);
    }

    @Transactional
    public Record deleteRecord(Long id) {
        final Record currentRecord = findRecord(id);
        recordRepository.delete(currentRecord);
        return currentRecord;
    }

    @Transactional
    public void deleteAllRecords() {
        recordRepository.deleteAll();
    }
}
