package com.attence.dao;

import com.attence.pojo.Dormitory;

public interface DormitoryMapper {

    int deleteByPrimaryKey(String dorId);

    int insert(Dormitory record);

    int insertSelective(Dormitory record);

    Dormitory selectByPrimaryKey(String dorId);

    int updateByPrimaryKeySelective(Dormitory record);

    int updateByPrimaryKeyWithBLOBs(Dormitory record);

    int checkDorNum(String dorId);
}