package com.attence.service.impl;

import com.attence.common.Const;
import com.attence.service.ITeacherService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by zhangsiqi on 2018/4/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public class TeacherServiceImplTest {

    @Autowired
    private ITeacherService iTeacherService;

    @Test
    public void login() throws Exception {
        int id = 789269;
    }

}