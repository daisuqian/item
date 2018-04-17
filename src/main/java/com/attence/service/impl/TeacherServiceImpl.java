package com.attence.service.impl;

import com.attence.common.Const;
import com.attence.common.ResponseCode;
import com.attence.common.ServerResponse;
import com.attence.dao.*;
import com.attence.pojo.*;
import com.attence.pojo.Class;
import com.attence.pojo.Teacher;
import com.attence.service.ITeacherService;
import com.attence.utils.ReadExcel;
import com.attence.vo.StudentDetail;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangsiqi
 * @create 2018-04-08-14:49
 */

@Service("iTeacherService")
public class TeacherServiceImpl implements ITeacherService {

    @Autowired
    private TeacherLoginMapper teacherLoginMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private DormitoryMapper dormitoryMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public ServerResponse<Teacher> login(Integer id, String password) {
        // 根据教师号获取教师信息
        Teacher teacher = teacherMapper.selectByPrimaryKey(id);
        if (teacher == null) {
            return ServerResponse.createByErrorMessage("ID_DOESN'T_EXIST");
        }
        // 如果权限不是超管 禁止登录
        if (teacher.getPermission() != Const.permission.PER_ADMIN) {
            return ServerResponse.createByErrorMessage("NOT_HAVE_PERMISSION");
        }
        String passwordT = teacherLoginMapper.selectPasswordById(id);
        // 判断密码是否相等
        if (StringUtils.isNotBlank(passwordT) && StringUtils.equals(password, passwordT)) {
            return ServerResponse.createBySuccess("LOGIN_SUCCESS", teacher);
        } else {
            return ServerResponse.createByError();
        }
    }

    /**
     * 按照条件查询
     *
     * @param classNum
     * @param id
     * @param name
     * @param major
     * @return
     */
    @Override
    public ServerResponse studentList(int classNum, int id, String name, String major) {
        // 如果专业不为空
        if (StringUtils.isNotBlank(major)) {
            return majorList(major);
        } else {
            // 专业为空 班级号不为空
            if (classNum != 0) {
                return studentList(classNum);
            } else {
                // 专业为空 班级号为空 学号不为空
                if (id != 0) {
                    return studentDetail(id);
                } else {
                    // 专业为空 班级号为空 学号为空 姓名不为空
                    if (StringUtils.isNotBlank(name)) {
                        return studentDetail(name);
                    } else {
                        return ServerResponse.createByErrorMessage("EMPTY_ARGUMENT");
                    }
                }
            }
        }
    }

    /**
     * 查询班级里的学生信息
     *
     * @param classNum 班级代码
     * @return
     */
    @Override
    public ServerResponse<List<Student>> studentList(int classNum) {
        List<Student> students = studentMapper.selectListByClassNum(classNum);
        return ServerResponse.createBySuccess(students);
    }

    /**
     * 查询专业的学生信息
     *
     * @param major 专业
     * @return
     */
    @Override
    public ServerResponse<List<Student>> majorList(String major) {
        // 检索出这个专业下的所有班级
        List<Class> classList = classMapper.selectClassByMajor(major);
        // 使用链表来进行连接
        List<Student> linkList = new LinkedList<>();
        List<Student> studentList = null;
        ServerResponse<List<Student>> studentListResponse;
        int size = classList.size();
        for (int i = 0; i < size; i++) {
            Integer classNum = classList.get(i).getId();
            // 获取这个班级下的所有学生信息
            studentListResponse = studentList(classNum);
            studentList = studentListResponse.getData();
            int SlSize = studentList.size();
            for (int j = 0; j < SlSize; j++) {
                // 读取存入链表
                linkList.add(studentList.get(j));
            }
        }
        return ServerResponse.createBySuccess(linkList);
    }

    /**
     * 查询学号对应学生的详细信息
     * id 唯一所以一次检索一个学生信息
     *
     * @param id 学号
     * @return
     */
    @Override
    public ServerResponse studentDetail(int id) {

        Student student = studentMapper.selectByPrimaryKey(id);
        Class clazz = classMapper.selectByPrimaryKey(student.getClassNum());
        // 创建详细信息学生的VO
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setSchool(clazz.getSchool());
        studentDetail.setDepartment(clazz.getDepartment());
        studentDetail.setMajor(clazz.getMajor());
        studentDetail.setClassNum(clazz.getId());
        studentDetail.setId(student.getId());
        studentDetail.setDorNum(student.getDorNum());
        studentDetail.setName(student.getName());
        studentDetail.setSex(student.getSex());
        studentDetail.setAge(student.getAge());
        studentDetail.setStatus(student.getStatus());
        return ServerResponse.createBySuccess(studentDetail);
    }


    /**
     * 查询姓名对应的学生详细信息
     *
     * @param name
     * @return
     */
    @Override
    public ServerResponse<List<Student>> studentDetail(String name) {
        // 因为名字可能会有重复的所以检索出List
        List<Student> studentList = studentMapper.selectByName(name);
        return ServerResponse.createBySuccess(studentList);
    }

    @Override
    public ServerResponse<List<Student>> deleteStudentByID(int id) {
        // 先查询被删除的人班级号
        int classNum = studentMapper.selectClassNumByID(id);
        // 然后删除
        int count = studentMapper.deleteByPrimaryKey(id);
        // id 不存在
        if (count == 0) {
            return ServerResponse.createByErrorMessage("DELETE_ERROR");
        }
        // 返回删除后的List
        return studentList(classNum);
    }

    @Override
    public ServerResponse<Student> addStudent(int id, int classNum, String dorNum,
                                              String name, String sex, int age, int status) {
        // 当学号不存在班级号和寝室号都存在即可插入信息
        ServerResponse response = checkValid(Const.STUDENT_ID, String.valueOf(id));
        if (!response.isSuccess()) {
            return response;
        }
        response = checkValid(Const.STUDENT_CLASS_NUM, String.valueOf(classNum));
        if (!response.isSuccess()) {
            return response;
        }
        response = checkValid(Const.STUDENT_DOR_NUM, dorNum);
        if (!response.isSuccess()) {
            return response;
        }
        // 如果性别不是"男","女"
        boolean s = !StringUtils.equals(Const.MALE, sex) && !StringUtils.equals(Const.FEMALE, sex);
        // 如果年龄小于零大于100
        boolean a = age < 0 || age > 100;
        if (s || a) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 校验完成开始插入信息
        Student student = new Student(id, classNum, dorNum, name, sex, age, status);
        int resultCount = studentMapper.insert(student);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess(student);
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<Student> updateStudent(int id, int classNum, String dorNum,
                                                 String name, String sex, int age, int status) {
        ServerResponse response = checkValid(Const.STUDENT_ID, String.valueOf(id));
        // if执行说明学号不存在
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("ID_IS_NOT_EXIST");
        }
        response = checkValid(Const.STUDENT_CLASS_NUM, String.valueOf(classNum));
        // if 执行说明教室不存在
        if (!response.isSuccess()) {
            return response;
        }
        // if 执行说明寝室不存在
        response = checkValid(Const.STUDENT_DOR_NUM, dorNum);
        if (!response.isSuccess()) {
            return response;
        }
        // 如果性别不是"男","女"
        boolean s = StringUtils.isNotBlank(sex) && !StringUtils.equals(Const.MALE, sex) && !StringUtils.equals(Const.FEMALE, sex);
        // 如果年龄小于零大于100
        boolean a = age < 0 || age > 100;
        if (s || a) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 校验完成开始修改信息
        Student student = new Student(id, classNum, dorNum, name, sex, age, status);
        int resultCount = studentMapper.updateByPrimaryKeySelective(student);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess(student);
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<String> batchImportInfo(String filename, MultipartFile file, String path) {
        boolean b = false;
        //解析excel
        ReadExcel readExcel = new ReadExcel();
        Map<String, List> map = readExcel.getExcelInfo(filename, file, path);
        // 从Excel解析信息完成
        if (map.size() == 0) {
            return ServerResponse.createByErrorMessage("UPLOAD_ERROR");
        }


        // 插入学生信息
        List classList = map.get("class");
        List studentList = map.get("student");
        if (classList != null && studentList != null) {
            addStudentAndClass(studentList, classList);
        }

        // 插入寝室信息
        List dormitoryList = map.get("dormitory");
        List equipmentList = map.get("equipment");
        if (dormitoryList != null) {
            addDormitory(dormitoryList);
        }
        // 如果设备信息不为空
        if (equipmentList != null) {
            addEquipment(equipmentList);
        }
        return ServerResponse.createBySuccessMessage("UPLOAD_SUCCESS");
    }


    private ServerResponse checkValid(String type, String str) {
        // 判断类型
        boolean suc = StringUtils.isNotBlank(type) && StringUtils.isNotBlank(str) &&
                StringUtils.equals(type, Const.STUDENT_ID)
                || StringUtils.equals(type, Const.STUDENT_CLASS_NUM)
                || StringUtils.equals(type, Const.STUDENT_DOR_NUM);
        if (suc) {
            //开始校验
            if (Const.STUDENT_ID.equals(type)) {
                // 检查学号是否存在
                int resultCount = studentMapper.checkStudentID(Integer.valueOf(str));
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("ID_IS_ALREADY_EXIST");
                }
            }



                // 检查班级号是否存在
                if (Const.STUDENT_CLASS_NUM.equals(type)) {
                    int resultCount = classMapper.checkClassNum(Integer.valueOf(str));
                    // 班级号不存在
                    if (resultCount == 0) {
                        return ServerResponse.createByErrorMessage("CLASS_NUM_NOT_EXIST");
                    }
                }
                // 检查寝室号是否存在
                if (Const.STUDENT_DOR_NUM.equals(type)) {
                    int resultCount = dormitoryMapper.checkDorNum(str);
                    if (resultCount == 0) {
                        return ServerResponse.createByErrorMessage("DOR_NUM_NOT_EXIST");
                    }
                }
            } else {
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                        ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            return ServerResponse.createBySuccessMessage("SUCCESS");
        }

        private ServerResponse addStudentAndClass (List<Student>studentList, List<Class>classList){
            int size;
            // 先插入班级
            size = classList.size();
            for (int i = 0; i < size; i++) {
                Class clazz = classList.get(i);
                int resultCount = classMapper.checkClassNum(clazz.getId());
                // 如果班级号已存在证明插入过, 直接跳过
                if (resultCount > 0) {
                    continue;
                }
                classMapper.insert(clazz);
            }
            size = studentList.size();
            for (int i = 0; i < size; i++) {
                Student student = studentList.get(i);
                int resultCount = studentMapper.checkStudentID(student.getId());
                if (resultCount > 0) {
                    continue;
                }
                studentMapper.insertSelective(student);
            }

            return ServerResponse.createBySuccessMessage("INSERT_SUCCESS");
        }

        private ServerResponse addDormitory (List < Dormitory > dormitoryList) {
            int size = dormitoryList.size();
            for (int i = 0; i < size; i++) {
                Dormitory dormitory = dormitoryList.get(i);
                int resultCount = dormitoryMapper.checkDorNum(dormitory.getDorId());
                // 如果寝室号已存在就将设备号添加进去
                if (resultCount > 0) {
                    dormitoryMapper.updateByPrimaryKeySelective(dormitory);
                } else {
                    // 添加新寝室和设备号
                    dormitoryMapper.insertSelective(dormitory);
                }
            }
            return ServerResponse.createBySuccessMessage("INSERT_SUCCESS");
        }

        private ServerResponse addEquipment (List < Equipment > equipmentList) {
            int size = equipmentList.size();
            for (int i = 0; i < size; i++) {
                Equipment equipment = equipmentList.get(i);
                int resultCount = equipmentMapper.checkEquipmentId(equipment.getId());
                if (resultCount > 0) {
                    continue;
                }
                equipmentMapper.insertSelective(equipment);
            }
            return ServerResponse.createBySuccessMessage("INSERT_SUCCESS");
        }
/**---------------Date 2018 4 16 至 4 17-------------------**/
        /**
         * 添加辅导员信息
         * @param id 职工号
         * @param school 学校名称
         * @param department 院系
         * @param name 姓名
         * @param permission 权限 (辅导员：1  超级管理员：2)
         **/
        @Override
        public ServerResponse<Teacher> addTeacher ( int id, String school, String department, String name,int permission)
        {

            Teacher teacher = new Teacher(id, school, department, name, permission);
            int resultCount = teacherMapper.insert(teacher);
            if (resultCount > 0) {
                return ServerResponse.createBySuccess(teacher);
            }
            return ServerResponse.createByError();
        }

        /**
         * 获取所有辅导员信息
         * @return
         */
        @Override
        public ServerResponse<List<Teacher>> getAll () {
            List<Teacher> teachers = teacherMapper.getAll();
            return ServerResponse.createBySuccess(teachers);
        }

        /**
         * 通过职工号删除辅导员信息
         * @Param id 职工号
         **/
        @Override
        public ServerResponse<List<Teacher>> deleteTeacherByID ( int id){

            int count = teacherMapper.deleteByPrimaryKey(id);

            if (count == 0) {
                return ServerResponse.createByErrorMessage("DELETE_ERROR");
            }
            //return ServerResponse.createBySuccessMessage("DELETE_SUCCESS");
            return getAll();
        }

        /**
         * 通过职工号辅导员查看自己的信息
         * @param id 职工号
         * @return
         */

        @Override
        public ServerResponse<Teacher> teacherList ( int id){
            Teacher teacher = teacherMapper.selectByPrimaryKey(id);
            if (teacher == null) {
                return ServerResponse.createByErrorMessage("ID_DOESN'T_EXIST");
            }
            return ServerResponse.createBySuccess("Your_INFORMATION", teacher);
        }

        /**
         * 通过辅导员的职工号和姓名查询其信息
         * @param id 职工号
         * @param name 姓名
         * @return
         */
        @Override
        public ServerResponse<Teacher> selectByIdAndName ( int id, String name)
        {
            Teacher teacher = teacherMapper.selectByIdAndName(id, name);
            return ServerResponse.createBySuccess("TEACHER_INFORMATION", teacher);
        }


    @Override
    public ServerResponse<Teacher> updateTeacher(int id,String school,String department,
                                                 String name,int permission) {
        ServerResponse response = checkValid2(Const.TEACHER_ID, String.valueOf(id));
        // if执行说明职工号不存在
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("ID_IS_NOT_EXIST");
        }

        // 校验完成开始修改信息
        Teacher teacher = new Teacher(id,school,department,name,permission);
        int resultCount = teacherMapper.updateByPrimaryKeySelective(teacher);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess(teacher);
        }
        return ServerResponse.createByError();
    }




    private ServerResponse checkValid2(String type, String str) {
        // 判断类型
        boolean suc = StringUtils.isNotBlank(type) && StringUtils.isNotBlank(str) &&
                StringUtils.equals(type, Const.TEACHER_ID);
        if (suc) {
            //开始校验
            if (Const.TEACHER_ID.equals(type)) {
                // 检查职工号是否存在
                int resultCount = teacherMapper.checkTeacherId(Integer.valueOf(str));
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("ID_IS_ALREADY_EXIST");
                }
            }

        } else {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return ServerResponse.createBySuccessMessage("SUCCESS");
    }



}

