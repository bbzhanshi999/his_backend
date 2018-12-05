package com.example.backend.app.web;

import com.example.backend.app.dao.MedicalRecordRepository;
import com.example.backend.app.entity.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/wx")
public class WxController {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @GetMapping("mr/{code}/{status}")
    public Mono<MedicalRecord> medicalRecord(@PathVariable("code") String code, @PathVariable("status") String status){
        if("3".equals(status)){
            return medicalRecordRepository.findByCode(code);
        }else{
            return medicalRecordRepository.findByCodeAndStatus(code,status);
        }
    }
}
