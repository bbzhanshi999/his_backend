package com.example.backend.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor // 生成所有参数构造方法
@NoArgsConstructor
@Document
public class Drug {

    @Id
    private String id;
    private String drugCode;
    private Double price;
    private String drugName;
    private String unit;

}
