package com.attence.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangsiqi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    private Integer id;

    private String school;

    private String department;

    private String major;
}