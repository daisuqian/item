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
public class Student {
    private Integer id;

    private Integer classNum;

    private String dorNum;

    private String name;

    private String sex;

    private Integer age;

    private Integer status;

}