package com.example.backend.app.dao;

import com.example.backend.app.entity.Drug;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface DrugRepository extends ReactiveCrudRepository<Drug,String> {


    public Flux<Drug> findByDrugCodeLike(String drugCode);
}
