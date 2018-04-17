package com.attence.service;

import com.attence.common.ServerResponse;
import com.attence.pojo.Student;
import com.attence.pojo.Teacher;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhangsiqi
 * Created by zhangsiqi on 2018/4/8
 */
public interface ITeacherService {

    ServerResponse<Teacher> login(Integer id, String password);

    ServerResponse<List<Student>> studentList(int classNum);

    ServerResponse<List<Student>> studentList(int classNum, int id, String name, String major);

    ServerResponse<List<Student>> majorList(String major);

    ServerResponse<List<Student>> studentDetail(int id);

    ServerResponse<List<Student>> studentDetail(String name);

    ServerResponse<List<Student>> deleteStudentByID(int id);

    ServerResponse<Student> addStudent(int id, int classNum, String dorNum,
                                       String name, String sex, int age, int status);

    ServerResponse<Student> updateStudent(int id, int classNum, String dorNum,
                                          String name, String sex, int age, int status);

    ServerResponse<String> batchImportInfo(String filename, MultipartFile file, String path);

    /*
     * Date:2018/4/16
     * Name:韩向领
     */
    ServerResponse<Teacher> addTeacher(int id,String school,String department,
                                       String name,int permission);

    ServerResponse<List<Teacher>> deleteTeacherByID(int id);
}
