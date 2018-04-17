package com.attence.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zhangsiqi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    private Date date;

    private String actor;

    private String id;
}