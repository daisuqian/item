package com.attence.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangsiqi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetail {

    private String school;

    private String department;

    private String major;

    private Integer id;

    private Integer classNum;

    private String dorNum;

    private String name;

    private String sex;

    private Integer age;

    private Integer status;

}