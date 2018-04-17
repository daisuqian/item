/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50551
Source Host           : localhost:3306
Source Database       : dormitory

Target Server Type    : MYSQL
Target Server Version : 50551
File Encoding         : 65001

Date: 2018-04-07 18:12:47
*/
USE attence;
SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id`        INT         NOT NULL
  COMMENT '学号',
  `class_num` INT         NOT NULL
  COMMENT '班级号',
  `dor_num`   VARCHAR(10) NOT NULL
  COMMENT '宿舍号',
  `name`      VARCHAR(20) NOT NULL
  COMMENT '姓名',
  `sex`       VARCHAR(5)  NOT NULL
  COMMENT '性别',
  `age`       INT         NOT NULL
  COMMENT '年龄',
  `status`    INT(4)      NOT NULL
  COMMENT '0-在校 1-请假 2-离校',
  PRIMARY KEY (`id`),
  KEY `class_num_index` (`class_num`) USING BTREE,
  KEY `dor_num_index` (`dor_num`) USING BTREE

)
  COMMENT '学生基本信息表'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id`         INT         NOT NULL
  COMMENT '辅导员号',
  `school`     VARCHAR(30) NOT NULL
  COMMENT '学校',
  `department` VARCHAR(20) NOT NULL
  COMMENT '院系',
  `name`       VARCHAR(20) NOT NULL
  COMMENT '姓名',
  `permission` INT(4)      NOT NULL
  COMMENT '0-只读, 1-部分可写全可读, 2-全可读写',
  PRIMARY KEY (`id`)
)
  COMMENT '辅导员基本信息表'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
  `id`         INT         NOT NULL
  COMMENT '班级号',
  `school`     VARCHAR(30) NOT NULL
  COMMENT '学校',
  `department` VARCHAR(20) NOT NULL
  COMMENT '院系',
  `major`      VARCHAR(20) NOT NULL
  COMMENT '专业',
  PRIMARY KEY (`id`)
)
  COMMENT '班级基本信息表'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `teach_class`;
CREATE TABLE `teach_class` (
  `id`         INT NOT NULL AUTO_INCREMENT
  COMMENT '主键',
  `teacher_id` INT NOT NULL
  COMMENT '辅导员号',
  `class_id`   INT NOT NULL
  COMMENT '班级号',
  PRIMARY KEY (`id`)
)
  COMMENT '辅导员管理的班级表'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `dormitory`;
CREATE TABLE `dormitory` (
  `dor_id`   VARCHAR(10) NOT NULL
  COMMENT '宿舍号-例如: 11#101',
  `equip_id` LONG DEFAULT NULL
  COMMENT '宿舍对应的设备号',
  PRIMARY KEY (`dor_id`)
)
  COMMENT '宿舍信息表'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment` (
  `id`    LONG NOT NULL
  COMMENT '设备号',
  `date`  DATETIME    DEFAULT NULL
  COMMENT '设备出厂日期',
  `actor` VARCHAR(20) DEFAULT NULL
  COMMENT '设备关联操作员'
)
  COMMENT '设备信息表'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `teacher_login`;
CREATE TABLE `teacher_login` (
  `id`       INT         NOT NULL
  COMMENT '教师号',
  `password` VARCHAR(16) NOT NULL
  COMMENT '密码',
  `phone`    VARCHAR(11) DEFAULT NULL
  COMMENT '电话'
)
  COMMENT '教师账号信息'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `student_login`;
CREATE TABLE `student_login` (
  `id`         INT         NOT NULL
  COMMENT '学号',a
  `password`   VARCHAR(16) NOT NULL
  COMMENT '密码',
  `phone`      VARCHAR(11) DEFAULT NULL
  COMMENT '电话',
  `eigenvalue` VARCHAR(20) DEFAULT NULL
  COMMENT '人脸特征值'
)
  COMMENT '学生账号信息'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;