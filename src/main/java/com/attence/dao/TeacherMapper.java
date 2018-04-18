package com.attence.dao;

import com.attence.pojo.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeacherMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Teacher record);

    int insertSelective(Teacher record);

    Teacher selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Teacher record);

    int updateByPrimaryKey(Teacher record);

    int checkTeacherId(Integer id);

    List<Teacher> getAll();

    Teacher selectByIdAndName(@Param(value="id") int id,@Param(value="name") String name);

    int updatePasswd(@Param(value="id") int id,@Param(value="password") String password);

}