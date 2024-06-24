package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.services.RecordService;
import com.example.hospital.WebConfiguration;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(WebConfiguration.REST_API + "/record")
public class RecordController {
    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/{id}")
    public RecordDTO getRecord(@PathVariable Long id) {
        return new RecordDTO(recordService.findRecord(id));
    }

    @GetMapping
        public List<RecordDTO> getRecords() {
        return recordService.findAllRecords().stream()
                .map(RecordDTO::new)
                .toList();
    }

    @PostMapping
    public RecordDTO createRecord(@RequestBody @Valid RecordDTO recordDTO) throws IOException {
        return new RecordDTO(recordService.addRecord(recordDTO));
    }

    @PutMapping("/{id}")
    public RecordDTO updateRecord(@PathVariable Long id, @RequestBody @Valid RecordDTO recordDTO) {
        return new RecordDTO(recordService.updateRecord(id, recordDTO));
    }

    @DeleteMapping("/{id}")
    public RecordDTO deleteRecord(@PathVariable Long id) {
        return new RecordDTO(recordService.deleteRecord(id));
    }

    @DeleteMapping
    public void deleteAll() {
        recordService.deleteAllRecords();
    }

}
