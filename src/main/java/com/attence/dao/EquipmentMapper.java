package com.attence.dao;

import com.attence.pojo.Equipment;

public interface EquipmentMapper {
    int insert(Equipment record);

    int insertSelective(Equipment record);

    int checkEquipmentId(String id);
}