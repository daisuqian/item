package com.attence.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;


import com.attence.common.Const;
import com.attence.controller.backend.TeacherController;
import com.attence.pojo.Dormitory;
import com.attence.pojo.Equipment;
import com.attence.pojo.Student;
import com.attence.pojo.Class;
import org.apache.commons.lang3.StringUtils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 读取Excel文件
 *
 * @author zhangsiqi
 * @create 2018-04-10-14:40
 */
public class ReadExcel {

    private static Logger logger = LoggerFactory.getLogger(TeacherController.class);

    /**
     * 总行数
     */
    private int totalRows = 0;
    /**
     * 总条数
     */
    private int totalCells = 0;

    /**
     * 构造方法
     */
    public ReadExcel() {
    }

    /**
     * 获取总行数
     */
    public int getTotalRows() {
        return totalRows;
    }

    /**
     * 获取总列数
     *
     * @return
     */
    public int getTotalCells() {
        return totalCells;
    }

    /**
     * 验证EXCEL文件
     *
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath) {
        boolean b = StringUtils.isBlank(filePath) || !(WDWUtil.isExcel2003(filePath) || WDWUtil.isExcel2007(filePath));
        if (b) {
            return false;
        }
        return true;
    }

    /**
     * 读EXCEL文件，获取学生信息集合
     *
     * @param fileName
     * @return
     */
    public Map<String, List> getExcelInfo(String fileName, MultipartFile mFile, String path) {

        //验证文件名是否合格
        if (!validateExcel(fileName)) {
            return null;
        }
        //根据文件名判断文件是2003版本还是2007版本
        boolean isExcel2003 = true;
        if (WDWUtil.isExcel2007(fileName)) {
            isExcel2003 = false;
        }

        // 扩展名 jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 组装上传文件名
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件, 上传文件的文件名{}, 上传的路径:{},新文件名:{}", fileName, path, uploadFileName);
        // 找到path所在的路径, 如果路径不存在则新建路径
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            // 赋予权限可写
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        // 指向目标文件 此时并没有真实文件
        File targetFile = new File(path, uploadFileName);
        try {
            mFile.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("上传文件成功, 上传文件的文件名{}, 上传的路径:{},新文件名:{}", fileName, path, uploadFileName);


        //初始化输入流
        InputStream is = null;
        Map<String, List> map = null;
        try {
            //根据新建的文件实例化输入流
            is = new FileInputStream(targetFile);
            //根据excel里面的内容读取信息
            map = this.getExcelInfo(is, isExcel2003);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 删除保留的文件
            targetFile.delete();
        }
        return map;
    }

    /**
     * 根据excel里面的内容读取信息
     *
     * @param is          输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     * @throws IOException
     */
    public Map<String, List> getExcelInfo(InputStream is, boolean isExcel2003) {
        Map<String, List> map = null;
        Workbook wb = null;
        try {
            //根据版本选择创建Workbook的方式
            //当excel是2003时
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {//当excel是2007时
                wb = new XSSFWorkbook(is);
            }
            //读取Excel里面的信息
            map = this.readExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 读取Excel里面的信息
     *
     * @param wb
     * @return
     */
    private Map<String, List> readExcelValue(Workbook wb) {

        Map<String, List> map = null;

        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);

        //得到Excel的行数
        this.totalRows = sheet.getPhysicalNumberOfRows();

        //得到Excel的列数(前提是有行数)
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        logger.info("当前Excel列数:{}", totalCells);
        // 学生Excel列数: 10   设备Excel列数: 4
        int studentExcel = 10, equipmentExcel = 4;
        if (this.totalCells == studentExcel) {
            map = readExcelStudentValue(sheet);
        } else if (this.totalCells == equipmentExcel) {
            map = readExcelEquipmentValue(sheet);
        }
        return map;
    }

    private Map<String, List> readExcelStudentValue(Sheet sheet) {
        Map<String, List> map = new HashMap<>(16);
        List<Class> classList = new ArrayList<>();
        List<Student> studentList = new ArrayList<>();
        List<Dormitory> dormitoryList = new ArrayList<>();
        Class clazz = null;
        Student student = null;
        Dormitory dormitory = null;
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            student = new Student();
            clazz = new Class();
            dormitory = new Dormitory();

            //循环Excel的列
            for (int c = 0; c < this.totalCells; c++) {
                Cell cell = row.getCell(c);
                if (null != cell) {

                    if (c == 0) {
                        // 学校
                        clazz.setSchool(cell.getStringCellValue());
                    } else if (c == 1) {
                        // 院系
                        clazz.setDepartment(cell.getStringCellValue());
                    } else if (c == 2) {
                        // 专业
                        clazz.setMajor(cell.getStringCellValue());
                    } else if (c == 3) {
                        // 班级代码
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        int classNum = Integer.valueOf(cell.getStringCellValue());
                        clazz.setId(classNum);
                        student.setClassNum(classNum);
                    } else if (c == 4) {
                        // 学号
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        student.setId(Integer.valueOf(cell.getStringCellValue()));
                    } else if (c == 5) {
                        //姓名
                        student.setName(cell.getStringCellValue());
                    } else if (c == 6) {
                        //年龄
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        student.setAge(Integer.valueOf(cell.getStringCellValue()));
                    } else if (c == 7) {
                        //性别
                        student.setSex(cell.getStringCellValue());
                    } else if (c == 8) {
                        // 寝室号
                        student.setDorNum(cell.getStringCellValue());
                        dormitory.setDorId(cell.getStringCellValue());
                    } else if (c == 9) {
                        String status = cell.getStringCellValue();
                        // 状态非空且在校
                        if (StringUtils.isNotBlank(status) &&
                                StringUtils.equals(status, Const.Status.STATUS_IN_SCHOOL.getValue())) {
                            student.setStatus(Const.Status.STATUS_IN_SCHOOL.getCode());
                        } else {
                            student.setStatus(Const.Status.STATUS_OUT_SCHOOL.getCode());
                        }
                    }

                }
            }
            if (student.getId() != null) {
                studentList.add(student);
            }
            // 默认可插入
            boolean insert = true;
            if (clazz.getId() != null) {
                for (int i = 0; i < classList.size(); i++) {
                    Class aClass = classList.get(0);
                    // 如果发现列表中已经包含了这个班级的信息
                    if (aClass.getId().equals(clazz.getId())) {
                        insert = false;
                        break;
                    }
                }
                if (insert) {
                    classList.add(clazz);
                }
            }
            if (StringUtils.isNotBlank(dormitory.getDorId())) {
                insert = true;
                for (int i = 0; i < dormitoryList.size(); i++) {
                    Dormitory aDormitory = dormitoryList.get(0);
                    // 如果发现列表中已经包含了这个班级的信息
                    if (aDormitory.getDorId().equals(dormitory.getDorId())) {
                        insert = false;
                        break;
                    }
                }
                if (insert) {
                    dormitoryList.add(dormitory);
                }
            }
        }
        map.put("student", studentList);
        map.put("class", classList);
        map.put("dormitory", dormitoryList);
        System.out.println("--------------------------------");
        for (Object student1 : studentList) {
            System.out.println(student1);
        }
        System.out.println("--------------------------------");
        for (Object aClass : classList) {
            System.out.println(aClass);
        }
        System.out.println("--------------------------------");
        for (Object dormitory1 : dormitoryList) {
            System.out.println(dormitory1);
        }
        return map;
    }

    private Map<String, List> readExcelEquipmentValue(Sheet sheet) {
        Map<String, List> map = new HashMap<>(16);
        List<Dormitory> dormitoryList = new ArrayList<>();
        List<Equipment> equipmentList = new ArrayList<>();
        Dormitory dormitory;
        Equipment equipment;
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            dormitory = new Dormitory();
            equipment = new Equipment();
            //循环Excel的列
            for (int c = 0; c < this.totalCells; c++) {
                Cell cell = row.getCell(c);
                if (null != cell) {

                    if (c == 0) {
                        // 寝室号
                        dormitory.setDorId(cell.getStringCellValue());
                    } else if (c == 1) {
                        // 设备号
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        dormitory.setEquipId(cell.getStringCellValue());
                        equipment.setId(cell.getStringCellValue());
                    } else if (c == 2) {
                        // 出厂日期
                        Date date = cell.getDateCellValue();
                        logger.info("Excel中的设备出场时间: {}", date);
                        equipment.setDate(date);
                    } else if (c == 3) {
                        // 操作员
                        equipment.setActor(cell.getStringCellValue());
                    }
                }

                // 默认可插入
                boolean insert;
                if (StringUtils.isNotBlank(dormitory.getDorId())) {
                    insert = true;
                    int size = dormitoryList.size();
                    for (int i = 0; i < size; i++) {
                        Dormitory aDormitory = dormitoryList.get(i);
                        // 如果发现列表中已经包含了这个班级的信息
                        if (aDormitory.getDorId().equals(dormitory.getDorId())) {
                            insert = false;
                            break;
                        }
                    }
                    if (insert) {
                        dormitoryList.add(dormitory);
                    }
                }
                if (StringUtils.isNotBlank(equipment.getId())) {
                    insert = true;
                    int size = equipmentList.size();
                    for (int i = 0; i < size; i++) {
                        Equipment aEquipment = equipmentList.get(i);
                        if (aEquipment.getId().equals(equipment.getId())) {
                            insert = false;
                            break;
                        }
                    }
                    if (insert) {
                        equipmentList.add(equipment);
                    }
                }
            }
        }
        map.put("dormitory", dormitoryList);
        map.put("equipment", equipmentList);
        System.out.println("--------------------------------");
        for (Object dormitory1 : dormitoryList) {
            System.out.println(dormitory1);
        }
        System.out.println("--------------------------------");
        for (Object equipment1 : equipmentList) {
            System.out.println(equipment1);
        }
        return map;
    }
}
