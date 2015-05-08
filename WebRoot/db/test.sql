/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.0.20a-nt : Database - sbanhmswvsclxjvnfqrn
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `tbl_contacts` */

DROP TABLE IF EXISTS `tbl_contacts`;

CREATE TABLE `tbl_contacts` (
  `id` int(11) NOT NULL auto_increment,
  `logo` varchar(50) default NULL COMMENT '联系人照片',
  `name` varchar(100) default NULL COMMENT '姓名',
  `sex` varchar(5) default NULL COMMENT '性别：男、女',
  `telephone` varchar(100) default NULL COMMENT '手机号码',
  `qq` varchar(100) default NULL COMMENT 'QQ号码',
  `email` varchar(100) default NULL COMMENT '邮箱',
  `bornProvince` varchar(50) default NULL COMMENT '出生地籍贯',
  `workTitle` varchar(100) default NULL COMMENT '工作职称',
  `school` varchar(100) default NULL COMMENT '毕业院校',
  `homeAddress` varchar(100) default NULL COMMENT '家庭详细地址',
  `workAddress` varchar(100) default NULL COMMENT '详细工作地址',
  `knowAddress` varchar(100) default NULL COMMENT '相识地点',
  `knowTime` varchar(50) default NULL COMMENT '相识时间',
  `birthday` varchar(50) default NULL COMMENT '生日',
  `call` varchar(50) default NULL COMMENT '称呼',
  `weight` int(11) default '1' COMMENT '关系权重',
  `catType` int(11) default NULL COMMENT '分类，FK：tbl_contacts_type-->id',
  `remark` text COMMENT '备注',
  `createTime` datetime default NULL COMMENT '创建时间',
  `deleteFlag` int(11) default '1' COMMENT '删除标识：1:正常，2：删除',
  `deleteTime` datetime default NULL COMMENT '删除时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_contacts_photo` */

DROP TABLE IF EXISTS `tbl_contacts_photo`;

CREATE TABLE `tbl_contacts_photo` (
  `id` int(11) NOT NULL auto_increment,
  `catId` int(11) default NULL COMMENT '所属联系人',
  `photo` varchar(50) default NULL COMMENT '照片地址',
  `remark` varchar(200) default NULL COMMENT '照片备注',
  `createTime` datetime default NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_contacts_remark` */

DROP TABLE IF EXISTS `tbl_contacts_remark`;

CREATE TABLE `tbl_contacts_remark` (
  `id` int(11) NOT NULL auto_increment,
  `catId` int(11) default NULL COMMENT '所属联系人',
  `remark` text COMMENT '历史变动备注',
  `createTime` datetime default NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_contacts_type` */

DROP TABLE IF EXISTS `tbl_contacts_type`;

CREATE TABLE `tbl_contacts_type` (
  `id` int(11) NOT NULL auto_increment,
  `typeName` varchar(20) default NULL COMMENT '联系人分类名称',
  `createTime` datetime default NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_gp` */

DROP TABLE IF EXISTS `tbl_gp`;

CREATE TABLE `tbl_gp` (
  `id` int(11) NOT NULL auto_increment COMMENT '主键',
  `gpdm` varchar(8) default NULL COMMENT '股票代码',
  `gpdmInt` int(11) default NULL COMMENT '股票代码(整数)',
  `gsmc` varchar(20) default NULL COMMENT '公司名称',
  `gpjzqz` int(11) default NULL COMMENT '股票价值权重',
  `gpjg` decimal(10,2) default NULL COMMENT '股票价格',
  `zde` decimal(10,2) default NULL COMMENT '涨跌额',
  `zdbl` decimal(10,2) default NULL COMMENT '涨跌比率',
  `ltag` decimal(10,2) default NULL COMMENT '流通A股',
  `icbhy` varchar(100) default '' COMMENT 'ICB行业',
  `sssj` int(11) default NULL COMMENT '上市时间',
  `remark` text COMMENT '备注',
  `remarkTime` datetime default NULL COMMENT '备注--最新修改时间，或者股票最近查看时间',
  `updateType` varchar(200) default NULL COMMENT '更新类型',
  `updateTypeTime` datetime default NULL COMMENT '更新类型--最新修改时间',
  `haveDayKline` tinyint(4) default '0' COMMENT '是否有日K线数据(0:无，1：有)',
  PRIMARY KEY  (`id`),
  KEY `IX_gpdmInt` (`gpdmInt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_gp_kline` */

DROP TABLE IF EXISTS `tbl_gp_kline`;

CREATE TABLE `tbl_gp_kline` (
  `id` int(11) NOT NULL auto_increment,
  `gpdm` varchar(8) default '' COMMENT '股票代码',
  `gpdmInt` int(11) default '0' COMMENT '股票代码(整数)',
  `klineDay` int(11) default '0' COMMENT '日期',
  `kpPrice` decimal(6,2) default '0.00' COMMENT '开盘价',
  `zgPrice` decimal(6,2) default '0.00' COMMENT '最高价',
  `zdPrice` decimal(6,2) default '0.00' COMMENT '最低价',
  `spPrice` decimal(6,2) default '0.00' COMMENT '收盘价',
  `zdf` decimal(6,2) default '0.00' COMMENT '涨跌幅',
  `cjl` decimal(15,2) default '0.00' COMMENT '交易量(手)',
  `cje` decimal(15,2) default '0.00' COMMENT '交易额(元)',
  `klineType` int(11) default '1' COMMENT 'K线数据类型（1:日K，2:周K，3:月K）',
  `createTime` datetime default '2015-05-02 00:00:00' COMMENT '创建时间',
  PRIMARY KEY  (`id`),
  KEY `IX_gpdmInt` (`gpdmInt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_gp_remark` */

DROP TABLE IF EXISTS `tbl_gp_remark`;

CREATE TABLE `tbl_gp_remark` (
  `id` int(11) NOT NULL auto_increment COMMENT '主键',
  `gpdm` varchar(8) default NULL COMMENT '股票代码',
  `remark` text COMMENT '备注',
  `createTime` datetime default NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_task` */

DROP TABLE IF EXISTS `tbl_task`;

CREATE TABLE `tbl_task` (
  `id` int(11) NOT NULL auto_increment,
  `task` text COMMENT '任务内容',
  `taskType` int(11) default NULL COMMENT '类型：1：工作，2：生活，3：学习',
  `taskVolume` int(11) default NULL COMMENT '任务大小：1：临时，2：小型，3：中型，4：大型',
  `taskUrgency` int(11) default NULL COMMENT '紧急程度：1：一般，2：紧急，3：特急',
  `taskStatus` int(11) default NULL COMMENT '状态：1:没开始，2：进行中，3：完成，4：取消，5：失败',
  `taskCome` int(11) default NULL COMMENT '任务来源，外键关联，tbl_task_come-->id',
  `startTime` datetime default NULL COMMENT '任务要求开始时间',
  `endTime` datetime default NULL COMMENT '任务要求完成时间',
  `finishTime` datetime default NULL COMMENT '任务实际完成时间',
  `createTime` datetime default NULL COMMENT '任务实际创建时间',
  `remark` text COMMENT '备注',
  `deleteFlag` int(11) default '1' COMMENT '1:正常，2：删除',
  `deleteTime` datetime default NULL COMMENT '删除时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_task_come` */

DROP TABLE IF EXISTS `tbl_task_come`;

CREATE TABLE `tbl_task_come` (
  `id` int(11) NOT NULL auto_increment,
  `taskCome` varchar(100) default NULL COMMENT '任务来源',
  `status` int(11) default '1' COMMENT '状态，1：使用中，2：不用了',
  `createTime` datetime default NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_task_remark` */

DROP TABLE IF EXISTS `tbl_task_remark`;

CREATE TABLE `tbl_task_remark` (
  `id` int(11) NOT NULL auto_increment,
  `taskId` int(11) default NULL COMMENT '外键关联，tbl_task-->id',
  `remark` text COMMENT '备注',
  `createTime` datetime default NULL COMMENT '创建时间',
  `subTaskStatus` int(11) default '0' COMMENT '0：非子任务，1:进行中，2：取消，3：完成',
  `statusChangeTime` datetime default NULL COMMENT '如果是子任务，记录子任务完成或者取消时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tbl_user` */

DROP TABLE IF EXISTS `tbl_user`;

CREATE TABLE `tbl_user` (
  `userName` varchar(20) NOT NULL COMMENT '用户名',
  `userPass` varchar(50) default NULL COMMENT '用户密码',
  `userTheme` varchar(20) default NULL COMMENT '系统主题',
  `registerTime` datetime default NULL COMMENT '注册时间',
  `deleteFlag` int(11) default NULL COMMENT '1:正常，2：删除',
  `deleteTime` datetime default NULL COMMENT '删除时间',
  PRIMARY KEY  (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
