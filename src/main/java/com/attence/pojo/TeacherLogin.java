package com.attence.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 辅导员账号信息
 *
 * @author zhangsiqi
 * @create 2018-04-08-15:09
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherLogin {
    private Integer id;
    private String password;
    private String phone;
}
