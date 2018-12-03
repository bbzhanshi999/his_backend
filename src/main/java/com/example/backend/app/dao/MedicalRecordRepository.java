package com.example.backend.app.dao;

import com.example.backend.app.entity.MedicalRecord;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MedicalRecordRepository extends ReactiveCrudRepository<MedicalRecord,String> {

    Mono<MedicalRecord> findByCode(String code);

    @Query(value="{ 'status' : ?0 }", fields="{ 'selfPaying' : 1, 'amount' : 1}")
    Flux<MedicalRecord> findByStatus(String status);

    Mono<MedicalRecord> findByCodeAndStatus(String code,String status);

    Mono<MedicalRecord> findByCodeAndStatusNot(String code, String status);

    Mono<Long> countByStatus(String status);
}
