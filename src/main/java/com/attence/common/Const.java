package com.attence.common;

/**
 * 不变属性
 *
 * @author zhangsiqi
 * @create 2018-04-08-15:24
 */

public class Const {

    public static final String CURRENT_TEACHER = "current_teacher";
    public static final String STUDENT_ID = "id";
    public static final String TEACHER_ID = "id";
    public static final String STUDENT_CLASS_NUM = "class_num";
    public static final String STUDENT_DOR_NUM = "dor_num";
    public static final String MALE = "男";
    public static final String FEMALE = "女";

    public enum Status {
        // 在校
        STATUS_IN_SCHOOL(0, "在校"),
        // 请假
        STATUS_LEAVE(1, "请假"),
        // 离校
        STATUS_OUT_SCHOOL(2, "离校");

        private int code;
        private String value;

        Status(int code, String value) {
            this.code = code;
            this.value = value;
        }


        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

    }

    public interface permission {
        //普通用户
        int PER_STUDENT = 0;
        // 辅导员
        int PER_TEACHER = 1;
        // 超管
        int PER_ADMIN = 2;
    }
}
