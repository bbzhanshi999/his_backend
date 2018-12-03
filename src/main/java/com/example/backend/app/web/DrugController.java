package com.example.backend.app.web;

import com.example.backend.app.dao.DrugRepository;
import com.example.backend.app.entity.Drug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/drug")
public class DrugController {

    @Autowired
    private DrugRepository drugRepository;

    @GetMapping("/{drugCode}")
    public Flux<Drug> drug(@PathVariable("drugCode") String drugCode){
        return drugRepository.findByDrugCodeLike(drugCode);
    }

    @GetMapping("/find")
    public Mono<Drug> find(@RequestBody Drug drug){
        return Mono.just(new Drug());
    }
}
