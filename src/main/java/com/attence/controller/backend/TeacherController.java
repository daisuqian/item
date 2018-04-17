package com.attence.controller.backend;

import com.attence.common.Const;
import com.attence.common.ResponseCode;
import com.attence.common.ServerResponse;
import com.attence.pojo.Student;
import com.attence.pojo.Teacher;
import com.attence.service.ITeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * 辅导员管理
 *
 * @author zhangsiqi
 * @create 2018-04-08-14:48
 */

@Controller
@RequestMapping("/manage/teacher")
public class TeacherController {

    private static Logger log = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    private ITeacherService iTeacherService;

    /**
     * 用户登录
     *
     * @param id       用户名
     * @param password 密码
     * @param session  会话
     * @return 返回登录用户
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Teacher> login(String id, String password, HttpSession session) {
        ServerResponse<Teacher> response = iTeacherService.login(Integer.valueOf(id), password);
        // 如果登陆成功将 teacher放入session中
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_TEACHER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        ServerResponse response = loginSucc(session);
        if (!response.isSuccess()) {
            return response;
        }
        session.removeAttribute(Const.CURRENT_TEACHER);
        return ServerResponse.createBySuccess();
    }


    /**
     * 通过专业or班级代码or学号or名字
     * @param classNum 班级带吗
     * @param id 学号
     * @param name 姓名
     * @param major 专业
     * @param session
     * @return
     */

    @RequestMapping("student_list.do")
    @ResponseBody
    public ServerResponse<List<Student>> studentList(@RequestParam(value = "classNum", required = false,defaultValue = "0") int classNum,
                                                     @RequestParam(value = "id", required = false, defaultValue = "0") int id,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "major", required = false) String major, HttpSession session) {
        ServerResponse response = loginSucc(session);
        if (!response.isSuccess()) {
            return response;
        }
        return iTeacherService.studentList(classNum,id,name,major);
    }

    /**
     * 通过学号删除学生信息
     *
     * @param id      学号
     * @param session
     * @return
     */
    @RequestMapping(value = "delete_student.do")
    @ResponseBody
    public ServerResponse<List<Student>> deleteStudent(int id, HttpSession session) {
        ServerResponse response = loginSucc(session);
        if (!response.isSuccess()) {
            return response;
        }
        return iTeacherService.deleteStudentByID(id);
    }

    /**
     * 添加学生信息
     *
     * @param id       学号
     * @param classNum 班级代码
     * @param dorNum   寝室号
     * @param name     姓名
     * @param sex      性别
     * @param age      年龄
     * @param status   状态
     * @param session  会话
     * @return
     */
    @RequestMapping(value = "add_student.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Student> addStudent(int id, int classNum, String dorNum,
                                              String name, String sex, int age,
                                              @RequestParam(value = "status", defaultValue = "0") int status,
                                              HttpSession session) {
        ServerResponse response = loginSucc(session);
        if (!response.isSuccess()) {
            return response;
        }
        return iTeacherService.addStudent(id, classNum, dorNum, name, sex, age, status);
    }

    @RequestMapping(value = "update_student.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Student> updateStudent(int id, int classNum, String dorNum, String name, String sex, int age,
                                                 @RequestParam(value = "status", defaultValue = "0") int status,
                                                 HttpSession session) {
        ServerResponse response = loginSucc(session);
        if (!response.isSuccess()) {
            return response;
        }
        return iTeacherService.updateStudent(id, classNum, dorNum, name, sex, age, status);
    }

    /**
     * 批量添加-上传Excel 文件
     *
     * @param file    学生信息文件或设备信息文件
     * @param session session
     * @param request
     * @return
     */
    @RequestMapping(value = "batch_import_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse batchImport(@RequestParam(value = "filename", required = false) MultipartFile file,
                                      HttpSession session, HttpServletRequest request) {
        ServerResponse response = loginSucc(session);
        if (!response.isSuccess()) {
            return response;
        }
        //判断文件是否为空
        if (file == null) {
            return ServerResponse.createByErrorMessage("EMPTY_FILE");
        }
        //获取文件名
        String name = file.getOriginalFilename();
        //进一步判断文件是否为空（即判断其大小是否为0或其名称是否为null）
        long size = file.getSize();
        if (org.apache.commons.lang3.StringUtils.isBlank(name) && size == 0) {
            return ServerResponse.createByErrorMessage("EMPTY_FILE");
        }
        // 获取文件上传到Tomcat中的路径 upload
        String path = request.getSession().getServletContext().getRealPath("upload");
        //批量导入。参数：文件名，文件。
        ServerResponse<String> importResponse = iTeacherService.batchImportInfo(name, file, path);

        return importResponse;
    }


    private ServerResponse loginSucc(HttpSession session) {
        Teacher teacher = (Teacher) session.getAttribute(Const.CURRENT_TEACHER);
        if (teacher == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createBySuccessMessage("YES");
    }

    /**
     * @param id  职工号
     * @param school  学校名称
     * @param department 院系
     * @param name       姓名
     * @param permission  权限(辅导员的权限为1)
     * @param session
     * @return
     */
    @RequestMapping(value = "add_teacher.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Teacher> addTeacher(int id, String school, String department,
                                              String name,int permission,
                                              HttpSession session) {
        ServerResponse response = loginSucc(session);
        if (!response.isSuccess()) {
            return response;
        }
        return iTeacherService.addTeacher(id,school,department,name,permission);
    }

    /**
     * @param id  职工号
     * @param session
     * @return
     */

    @RequestMapping(value = "delete_teacher.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse<List<Teacher>> deleteTeacher(int id, HttpSession session) {
        ServerResponse response = loginSucc(session);
        if (!response.isSuccess()) {
            return response;
        }
        return iTeacherService.deleteTeacherByID(id);
    }

}
