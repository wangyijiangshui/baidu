package com.duapp.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.duapp.vo.Message;

public class Test {

	/**
	 * 输入参数：theStockCode = 股票代号，如：sh000001； 返回数据：一个一维字符串数组 String(24)，
	 * 	结构为：String(0)股票代号、String(1)股票名称、String(2)行情时间、String(3)最新价（元）、
	 * 		String(4)昨收盘（元）、String(5)今开盘（元）、String(6)涨跌额（元）、String(7)最低（元）、
	 * 		String(8)最高（元）、String(9)涨跌幅（%）、String(10)成交量（手）、String(11)成交额（万元）、
	 * 		String(12)竞买价（元）、String(13)竞卖价（元）、String(14)委比（%）、
	 * 		String(15)-String(19)买一 - 买五（元）/手、String(20)-String(24)卖一 - 卖五（元）/手。
	 * 
	 * 
	 * <ArrayOfString xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://WebXml.com.cn/">
	 * <string>sz002339</string>
	 * <string>积成电子</string>
	 * <string>2014-03-21 15:35:32</string>
	 * <string>9.91</string>
	 * <string>9.93</string>
	 * <string>9.83</string>
	 * <string>-0.02</string>
	 * <string>9.52</string>
	 * <string>9.98</string>
	 * <string>-0.20%</string>
	 * <string>43417.15</string>
	 * <string>4240.9553</string>
	 * <string>9.91</string>
	 * <string>9.92</string>
	 * <string>29.66%</string>
	 * <string>9.91 / 139.66</string>
	 * <string>9.90 / 456.95</string>
	 * <string>9.89 / 195.54</string>
	 * <string>9.88 / 510.57</string>
	 * <string>9.87 / 15.00</string>
	 * <string>9.92 / 148.80</string>
	 * <string>9.93 / 296.00</string>
	 * <string>9.94 / 106.00</string>
	 * <string>9.95 / 144.00</string>
	 * <string>9.96 / 19.99</string>
	 * </ArrayOfString>
	 * 
	 * @param request
	 * @param response
	 */
	public void refreshPageButton(HttpServletRequest request, HttpServletResponse response) {
		boolean result = false;
		String gpdmStr = request.getParameter("gpdmStr");
		String[] gpdms = null;
		Map<String, String> map = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			if(null != gpdmStr)gpdms = gpdmStr.split(",");
			String xmlDoc = null;
			String url = null;
			for (String gpdm : gpdms) {
				
				try {
					url = "http://www.webxml.com.cn/WebServices/ChinaStockWebService.asmx/getStockInfoByCode?theStockCode="+gpdm;
					if (null != gpdm && !"".equals(gpdm)) {
						xmlDoc = CommonUtil.getURLContent(url, "utf-8");
						Pattern p = Pattern.compile("<string>(.*?)</string>");
					    Matcher m = p.matcher(xmlDoc);
						int i = 0; 
						map = new HashMap<String, String>();
						map.put("gpdm", gpdm);
					    while(m.find()) {
					    	i = i + 1;
					    	if(2 == i) map.put("gsmc", m.group(1));//搜索股票名称
					    	if(4 == i) map.put("gpjg", m.group(1));//搜索股票价格
					    	if(7 == i) map.put("zde", m.group(1));//搜索股票涨跌额
					    	if(10 == i) map.put("zdbl", m.group(1));//搜索股票涨跌比率
					    	if(10 == i)break;
					    }
					    list.add(map);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result, CommonUtil.fromObjctToJson(list))), response);
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @param response
	 */
	public void refreshDbButton(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		Statement updateStmt = null;
		String sql = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			updateStmt = conn.createStatement();
			sql = "SELECT gpdm FROM tbl_gp";
			rs = stmt.executeQuery(sql);
			String gpdm = null;
			String xmlDoc = null;
			String url = null;
			int j = 0;
			while(rs.next()) {
				
				try {
					gpdm = rs.getString("gpdm");
					if (null != gpdm && !"".equals(gpdm)) {
						url = "http://www.webxml.com.cn/WebServices/ChinaStockWebService.asmx/getStockInfoByCode?theStockCode="+gpdm;
						xmlDoc = CommonUtil.getURLContent(url, "utf-8");
						Pattern p = Pattern.compile("<string>(.*?)</string>");
					    Matcher m = p.matcher(xmlDoc);
						int i = 0; 
						String gsmc = null;
						System.out.println(xmlDoc+"\n\n");
					    while(m.find()) {
					    	i = i + 1;
					    	//搜索股票名称
					    	if(2 == i){
					    		System.out.println(++j + " =" + gsmc+".");
					    		gsmc = m.group(1);
					    		updateStmt.executeUpdate("update tbl_gp set gsmc='"+m.group(1)+"' where gpdm='"+gpdm+"'");
					    	}
					    	//搜索股票价格
					    	if(4 == i) {
					    		updateStmt.executeUpdate("update tbl_gp set gpjg='"+m.group(1)+"' where gpdm='"+gpdm+"'");
					    	}
					    	//搜索股票涨跌额
					    	if(7 == i) {
					    		updateStmt.executeUpdate("update tbl_gp set zde='"+m.group(1)+"' where gpdm='"+gpdm+"'");
					    	}
					    	//搜索股票涨跌比率
					    	if(10 == i) {
					    		updateStmt.executeUpdate("update tbl_gp set zdbl='"+m.group(1)+"' where gpdm='"+gpdm+"'");
					    	}
					    	if(10 == i)break;
					    }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
			DBUtil.close(updateStmt, null);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}

	public static void main(String[]sraga) {
//		String s = "ss";
//		System.out.println(CommonUtil.fromObjctToJson(s));
//		Map map = new HashMap();
//		map.put("rr", "1111");
//		map.put("rr1", "2222");
//		System.out.println(CommonUtil.fromObjctToJson(map));
//		List list = new ArrayList<String>();
//		list.add("333");
//		list.add("444");
//		System.out.println(CommonUtil.fromObjctToJson(list));
		//System.out.println(CommonUtil.getURLContent("http://stockdata.stock.hexun.com/002471.shtml", "gb2312"));
		
		String xmlDoc = CommonUtil.getURLContent("http://stockdata.stock.hexun.com/002077.shtml", "gbk");
		System.out.println(xmlDoc+"\n\n\n");
		
		//<a href="http://quote.hexun.com/stock/icb.aspx?code=8&amp;name=金融业" target="_blank">不动产</a>
		Pattern p = Pattern.compile("quote.hexun.com/stock/icb.aspx\\?code=.*?>(.*?)</a>");
	    Matcher m = p.matcher(xmlDoc);
	    if(m.find()) {
	    	System.out.println("1======"+m.group(1));
	    }
	    p = Pattern.compile("stockdata.stock.hexun.com/gszl/jbzllinkPage.aspx\\?c=.*?>(.*?)</a>");
	    m = p.matcher(xmlDoc);
	    int i = 0;
	    while(m.find()) {
	    	if((i++)==1){
	    		System.out.println("2======"+m.group(1));
	    		break;
	    	}
	    }
	}
}
