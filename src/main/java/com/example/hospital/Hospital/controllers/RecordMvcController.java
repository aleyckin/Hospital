package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.models.enums.Place;
import com.example.hospital.Hospital.models.enums.Status;
import com.example.hospital.Hospital.services.DoctorService;
import com.example.hospital.Hospital.services.RecordService;
import com.example.hospital.Hospital.services.UserService;
import com.example.hospital.WebConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/record")
public class RecordMvcController {
    private final RecordService recordService;
    private final DoctorService doctorService;
    private final UserService userService;

    public RecordMvcController(RecordService recordService, DoctorService doctorService, UserService userService) {
        this.recordService = recordService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @GetMapping
    public String getRecords(Model model) {
        List<RecordDTO> recordDTOS;
        recordDTOS = recordService.findAllRecords().stream()
                .map(RecordDTO::new)
                .toList();
        model.addAttribute("records", recordDTOS);
        return "records";
    }

    @GetMapping(value = {"/update", "/update/{id}"})
    public String updateRecord(@PathVariable(required = false) Long id,
                                Model model) {
        model.addAttribute("doctors", doctorService.findAllDoctors());
        model.addAttribute("users", userService.findAllUsers());
        if (id == null || id <= 0) {
            model.addAttribute("recordDTO", new RecordDTO());
        } else {
            model.addAttribute("recordDTO", id);
            model.addAttribute("recordDTO", new RecordDTO(recordService.findRecord(id)));
        }
        return "record-update";
    }

    @PostMapping(value = {"/", "/{id}"})
    public String saveRecord(@PathVariable(required = false) Long id,
                              @RequestParam(value = "price") Double price,
                             @RequestParam(value = "place") Place place,
                             @RequestParam(value = "status") Status status,
                              @RequestParam(value = "doctorId") Long doctor_id,
                             @RequestParam(value = "userId") Long user_id,
                              @ModelAttribute("productDto") RecordDTO recordDTO,
                              BindingResult bindingResult,
                              Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors",
                    bindingResult.getAllErrors());
            return "record-update";
        }
        recordDTO.setPrice(price);
        recordDTO.setPlace(place);
        recordDTO.setStatus(status);
        recordDTO.setDoctor_id(doctor_id);
        recordDTO.setDoctorName(doctorService.findDoctor(recordDTO.getDoctor_id()).getName());
        recordDTO.setUser_id(user_id);
        recordDTO.setUserMail(userService.findUser(recordDTO.getUser_id()).getMail());
        if (id == null || id <= 0) {
            return "redirect:/record/" + recordService.addRecord(recordDTO).getId().toString();
        } else {
            recordService.updateRecord(id, recordDTO);
        }
        return "redirect:/record";
    }

    @PostMapping("/delete/{id}")
    public String deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return "redirect:/record";
    }

}
