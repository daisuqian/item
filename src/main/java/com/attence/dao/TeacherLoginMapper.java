package com.attence.dao;


import com.attence.pojo.TeacherLogin;

public interface TeacherLoginMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(TeacherLogin record);

    int insertSelective(TeacherLogin record);

    TeacherLogin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeacherLogin record);

    int updateByPrimaryKey(TeacherLogin record);

    String selectPasswordById(Integer id);
}