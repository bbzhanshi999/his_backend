package com.example.backend.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // 生成所有参数构造方法
@NoArgsConstructor
public class DrugInfo {

    private String id;
    private String drugCode;
    private Double price;
    private String drugName;
    private String unit;
    private Integer num;
    private Double amount;
    private Double selfPaying;
}
