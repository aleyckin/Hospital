package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.services.DoctorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/doctor")
public class DoctorMvcController {
    private final DoctorService doctorService;

    public DoctorMvcController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String getDoctors(Model model) {
        model.addAttribute("doctors", doctorService.findAllDoctors()
                .stream()
                .map(DoctorDTO::new)
                .toList());
        return "doctors";
    }

    @GetMapping(value = {"/update", "/update/{id}"})
    public String updateDoctor(@PathVariable(required = false) Long id,
                                 Model model) {
        if (id == null || id <= 0) {
            model.addAttribute("doctorDto", new DoctorDTO());
        } else {
            model.addAttribute("doctorDto", id);
            model.addAttribute("doctorDto", new DoctorDTO(doctorService.findDoctor(id)));
        }
        return "doctor-update";
    }

    @PostMapping(value = {"/", "/{id}"})
    public String saveDoctor(@PathVariable(required = false) Long id,
                               @ModelAttribute("doctorDto") DoctorDTO doctorDTO,
                               BindingResult bindingResult,
                               Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors",
                    bindingResult.getAllErrors());
            return "doctor-update";
        }
        if (id == null || id <= 0) {
            doctorService.addDoctor(doctorDTO);
        } else {
            doctorService.updateDoctor(id, doctorDTO);
        }
        return "redirect:/doctor";
    }

    @PostMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/doctor";
    }
}
