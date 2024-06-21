package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.services.DoctorService;
import com.example.hospital.WebConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(WebConfiguration.REST_API + "/doctor")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/{id}")
    public DoctorDTO getDoctor(@PathVariable Long id) {
        return new DoctorDTO(doctorService.findDoctor(id));
    }

    @GetMapping
    public List<DoctorDTO> getDoctors() {
        return doctorService
                .findAllDoctors()
                .stream()
                .map(DoctorDTO::new)
                .toList();
    }

    @PostMapping
    public DoctorDTO createDoctor(@RequestBody @Valid DoctorDTO doctorDTO) throws IOException {
        return new DoctorDTO(doctorService.addDoctor(doctorDTO));
    }

    @PutMapping("/{id}")
    public DoctorDTO updateDoctor(@PathVariable Long id, @RequestBody @Valid DoctorDTO doctorDTO) {
        return new DoctorDTO(doctorService.updateDoctor(id, doctorDTO));
    }

    @DeleteMapping("/{id}")
    public DoctorDTO deleteDoctor(@PathVariable Long id) {
        return new DoctorDTO(doctorService.deleteDoctor(id));
    }
}
