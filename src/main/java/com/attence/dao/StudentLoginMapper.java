package com.attence.dao;


import com.attence.pojo.StudentLogin;

public interface StudentLoginMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(StudentLogin record);

    int insertSelective(StudentLogin record);

    StudentLogin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StudentLogin record);

    int updateByPrimaryKey(StudentLogin record);

    String selectPasswordById(Integer id);
}