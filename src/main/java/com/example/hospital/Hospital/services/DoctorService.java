package com.example.hospital.Hospital.services;

import com.example.hospital.Hospital.controllers.DoctorDTO;
import com.example.hospital.Hospital.models.Doctor;
import com.example.hospital.Hospital.models.enums.Place;
import com.example.hospital.Hospital.repository.DoctorRepository;
import com.example.hospital.Util.Validation.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final ValidatorUtil validatorUtil;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, ValidatorUtil validatorUtil) {
        this.doctorRepository = doctorRepository;
        this.validatorUtil = validatorUtil;
    }

    @Transactional
    public Doctor addDoctor(DoctorDTO doctorDTO) throws IOException {
        final Doctor doctor = new Doctor(doctorDTO);
        validatorUtil.validate(doctor);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor addDoctor(String name, Place place) {
        final Doctor doctor = new Doctor(name, place);
        validatorUtil.validate(doctor);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public Doctor findDoctor(Long id) {
        final Optional<Doctor> client = doctorRepository.findById(id);
        return client.orElseThrow(() -> new DoctorNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public Doctor updateDoctor(Long id, String name, Place place) {
        final Doctor currentDoctor = findDoctor(id);
        currentDoctor.setName(name);
        currentDoctor.setPlace(place);
        validatorUtil.validate(currentDoctor);
        return doctorRepository.save(currentDoctor);
    }

    @Transactional
    public Doctor updateDoctor(Long id, DoctorDTO doctorDTO) {
        final Doctor currentDoctor = findDoctor(id);
        currentDoctor.setName(doctorDTO.getName());
        currentDoctor.setPlace(doctorDTO.getPlace());
        validatorUtil.validate(currentDoctor);
        return doctorRepository.save(currentDoctor);
    }

    @Transactional
    public Doctor deleteDoctor(Long id) {
        final Doctor currentDoctor = findDoctor(id);
        doctorRepository.delete(currentDoctor);
        return currentDoctor;
    }

    @Transactional
    public void deleteAllCategories() {
        doctorRepository.deleteAll();
    }
}
