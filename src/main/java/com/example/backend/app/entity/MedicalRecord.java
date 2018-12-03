package com.example.backend.app.entity;

import com.example.backend.sys.secure.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor // 生成所有参数构造方法
@NoArgsConstructor
@Document
public class MedicalRecord {


    public static final String PROCESSING="0";
    public static final String SETTLEMENT="1";
    public static final String TEMPORARY="2";

    @Id
    private String id;

    private String code;

    private String name;

    private String gender;

    private String doctor;

    private Integer age;

    private String depart;

    private String contract;

    private List<DrugInfo> drugInfoList;

    private String status;

    private String updateTime;

    private String operator;

    private Double amount;

    private Double selfPaying;
}
