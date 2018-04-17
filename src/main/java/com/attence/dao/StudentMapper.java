package com.attence.dao;

import com.attence.pojo.Student;

import java.util.List;

public interface StudentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

    List<Student> selectListByClassNum(int classNum);

    int selectClassNumByID(Integer id);

    int checkStudentID(Integer id);

    List<Student> selectByName(String name);
}