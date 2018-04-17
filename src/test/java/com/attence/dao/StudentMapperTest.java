package com.attence.dao;

import com.attence.pojo.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by zhangsiqi on 2018/4/4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public class StudentMapperTest {

    @Autowired
    private StudentMapper studentMapper;

    @Test
    public void selectByPrimaryKey() throws Exception {
        Student student = studentMapper.selectByPrimaryKey(1615925642);
        System.out.println(student);
    }

}