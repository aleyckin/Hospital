package com.example.hospital.Hospital.services;

import com.auth0.jwt.JWT;
import com.example.hospital.Hospital.controllers.RecordDTO;
import com.example.hospital.Hospital.models.Doctor;
import com.example.hospital.Hospital.models.Record;
import com.example.hospital.Hospital.models.User;
import com.example.hospital.Hospital.models.enums.Place;
import com.example.hospital.Hospital.models.enums.Specialization;
import com.example.hospital.Hospital.models.enums.Status;
import com.example.hospital.Hospital.repository.RecordRepository;
import com.example.hospital.Hospital.repository.UserRepository;
import com.example.hospital.Util.Validation.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//import static com.example.hospital.SecurityConfiguration.getAccessToken;

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
        List<Record> temp = recordRepository.findAll();
        return temp;
    }

    @Transactional
    public List<Record> findAllUserRecords(Long userId) {
        List<Record> records = recordRepository.findRecordsByUserId(userId);
        return records;
    }

    public User getUserFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userService.findByLogin(username);
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

    public Record updateRecordStatus(Long id, Status status) {
        Record record = findRecord(id);
        record.setStatus(status);
        return recordRepository.save(record);
    }

    public double determinePrice(Long doctorId) {
        Doctor doctor = doctorService.findDoctor(doctorId);
        return getPriceBySpecialization(doctor.getSpecialization());
    }

    private double getPriceBySpecialization(Specialization specialization) {
        switch (specialization) {
            case Cardiologist:
                return 100.0;
            case Dermatologist:
                return 80.0;
            case Ophthalmologist:
                return 120.0;
            case Dentist:
                return 50.0;
            default:
                return 30;
        }
    }
}
