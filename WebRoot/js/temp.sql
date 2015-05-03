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
  `remark` text COMMENT '备注',
  `remarkTime` datetime default NULL COMMENT '备注--最新修改时间，或者股票最近查看时间',
  `updateType` varchar(200) default NULL COMMENT '更新类型',
  `updateTypeTime` datetime default NULL COMMENT '更新类型--最新修改时间',
  PRIMARY KEY  (`id`),
  KEY `IX_gpdmInt` (`gpdmInt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE tbl_gp_kline;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

CREATE INDEX IX_gpdmInt ON tbl_gp (gpdmInt);
CREATE INDEX IX_gpdmInt ON tbl_gp_kline (gpdmInt);





SELECT MAX(id) FROM `tbl_gp_kline`
UPDATE `tbl_gp_kline` SET gpdmInt=gpdm+1000000

SELECT gpdm FROM `tbl_gp` WHERE gpdmInt NOT IN (SELECT gpdmInt FROM tbl_gp_kline)
SELECT gpdmInt FROM tbl_gp a WHERE NOT EXISTS (SELECT gpdmInt FROM tbl_gp_kline WHERE gpdmInt=a.gpdmInt)


