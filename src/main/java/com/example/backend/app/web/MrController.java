package com.example.backend.app.web;

import com.example.backend.app.dao.MedicalRecordRepository;
import com.example.backend.app.entity.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/medicalRecord")
public class MrController {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @GetMapping("{code}/{status}")
    public Mono<MedicalRecord> medicalRecord(@PathVariable("code") String code,@PathVariable("status") String status){
        if("3".equals(status)){
            return medicalRecordRepository.findByCode(code);
        }else{
            return medicalRecordRepository.findByCodeAndStatus(code,status);
        }
    }

    @PostMapping(value = "save",consumes = "application/json")
    public Mono<MedicalRecord> save(@RequestBody MedicalRecord medicalRecord){
        return medicalRecordRepository.save(medicalRecord);
    }

    @GetMapping("amount")
    public Flux<MedicalRecord> amount(){
        return medicalRecordRepository.findByStatus("1");
    }
}
