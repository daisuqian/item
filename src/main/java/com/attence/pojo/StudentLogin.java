package com.attence.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生账号信息
 *
 * @author zhangsiqi
 * @create 2018-04-08-15:09
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentLogin {
    private Integer id;
    private String password;
    private String phone;
    private String eigenvalue;
}
