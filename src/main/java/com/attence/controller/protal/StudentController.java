package com.attence.controller.protal;

import com.attence.dao.StudentMapper;
import com.attence.pojo.Student;
import com.attence.service.IStudentService;
import org.codehaus.jackson.JsonGenerationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 学生信息管理
 *
 * @author zhangsiqi
 * @create 2018-04-08-14:30
 */

@Controller
@RequestMapping("/student/")
public class StudentController {

    @Autowired
    private IStudentService iStudentService;

}
