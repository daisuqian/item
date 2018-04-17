package com.attence.dao;

import com.attence.pojo.TeachClass;

public interface TeachClassMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TeachClass record);

    int insertSelective(TeachClass record);

    TeachClass selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeachClass record);

    int updateByPrimaryKey(TeachClass record);
}