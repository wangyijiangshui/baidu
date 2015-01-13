package com.duapp.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**
 * 常用工具方法类，提供一个静态的常用方法
 * 
 * @author JS
 * @date	2014-03-19
 */
public class CommonUtil {

	/**
	 * 从指定的字符串中截取指定长度的子字符串，如果不够长度直接返回原字符串，如果超过长度，则截取指定长度的子字符串
	 * 并在末尾添加：...
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String subString(String str, int length) {
		if (null == str || "".equals(str)) {
			return "......";
		} else if (str.length()>length) {
			return str.substring(0,length)+"...";
		} else {
			return str;
		}
	}
	
	/**
	 * 
	 * 
	 * @param website
	 * @param gpdm
	 * @return
	 */
	public static String getGpUrl(String website, String gpdm) {
		//如果采用和讯网
		if("hexun".equals(website)) {
			return "http://stockdata.stock.hexun.com/"+gpdm+".shtml";
		//同花顺
		} else if ("ths".equals(website)) {
			return "http://stockpage.10jqka.com.cn/"+gpdm+"/";
		//新浪
		} else if("sina".equals(website)) {
			return "http://finance.sina.com.cn/realstock/company/"+gpdm+"/nc.shtml";
		} else {
			return "#";
		}
	}
	
	/**
	 * 将指定的数据写到客户端
	 * 非特殊情况或者特殊需求，请默认采用对应的重载方法：sendJsonDataToClient(Object obj, HttpServletResponse response)
	 * 只有该重载方法不能满足需求时，再采用该方法
	 * 
	 * @param jsonData
	 * @param response
	 */
	public static void sendJsonDataToClient(String jsonData, HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		try {
			response.getWriter().write(jsonData);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将指定的数据写到客户端
	 * 格式化日期字段值时默认采用模式：yyyy-MM-dd，如果需要其他模式，
	 * 请采用重载方法：sendJsonDataToClient(Object obj, String dateFmt, HttpServletResponse response)以便自行指定模式
	 * 
	 * @param obj
	 * @param response
	 */
	public static void sendJsonDataToClient(Object obj, HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		try {
			response.getWriter().write(CommonUtil.fromObjctToJson(obj, null));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将指定的数据写到客户端
	 * 
	 * @param obj
	 * @param dateFmt	格式化日期字段时采用的“模式”
	 * @param response
	 */
	public void sendJsonDataToClient(Object obj, String dateFmt, HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		try {
			response.getWriter().write(CommonUtil.fromObjctToJson(obj, dateFmt));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /** 
     * 按指定格式格式化指定日期（默认为yyyy-MM-dd HH:mm:ss） 
     *  
     * @param calendar	 将要格式化的日历对象
     * @param formatStr formatStr 格式化日期对象的模式，不指定默认为：yyyy-MM-dd HH:mm:ss
     *  
     * @return 格式化后的字符串
     */  
    public static String formatCalendar(Calendar calendar, String formatStr) {  
        SimpleDateFormat sdf = null;  
        String result = null;  
        if(null == formatStr || "".equals(formatStr))formatStr = "yyyy-MM-dd HH:mm:ss";  
        if (null != calendar) {  
            sdf = new SimpleDateFormat(formatStr);  
            result = sdf.format(calendar.getTime());  
            sdf = null;  
        }  
        return result;  
    } 
    
    /** 
     * 按指定格式格式化指定日期（默认为yyyy-MM-dd HH:mm:ss） 
     *  
     * @param date	 将要格式化的日期对象
     * @param formatStr formatStr 格式化日期对象的模式，不指定默认为：yyyy-MM-dd HH:mm:ss
     *  
     * @return 格式化后的字符串
     */  
    public static String formatDate(Date date, String formatStr) {  
        SimpleDateFormat sdf = null;  
        String result = null;  
        if(null == formatStr || "".equals(formatStr))formatStr = "yyyy-MM-dd HH:mm:ss";  
        if (null != date) {  
            sdf = new SimpleDateFormat(formatStr);  
            result = sdf.format(date);  
            sdf = null;  
        }  
        return result;  
    }
    
    /**
	 * 将对象格式化成Json字符串并返回
	 * 
	 * @param obj	需要格式化的对象
	 * @param dateFmt	格式化日期字段时采用的“模式”
	 * 
	 * @return	如果obj为空，则返回空的json对象，即“{}”，如果是字符串则返回字符串本身，
	 * 			如果是数组或者集合类对象，则采用JSONArray对象格式化成json中的数组对象，如果不满足以上情况，则默认全部采用JSONObject格式化成json普通对象
	 */
	public static String fromObjctToJson(Object obj, String dateFmt) {
		if(null == dateFmt || "".equals(dateFmt.trim()))dateFmt = "yyyy-MM-dd";
		JsonConfig config = new JsonConfig();  
		DateJsonValueProcessor processor = new DateJsonValueProcessor(dateFmt);
        config.registerJsonValueProcessor(java.util.Date.class, processor);
        config.registerJsonValueProcessor(java.sql.Date.class, processor);
		String json = null;
		if (null == obj) {
			json = "{}";
		} else if (obj  instanceof String) {
			json = obj.toString();
		} else if (obj.getClass().isArray() || obj instanceof Collection) {
			json=JSONArray.fromObject(obj,config).toString();
		} else {
			json=JSONObject.fromObject(obj,config).toString();
		}
		return json.replace("null", "\"\"");
	}
	
	/**
	 * 将对象格式化成Json字符串并返回
	 * 格式化日期字段值时默认采用模式：yyyy-MM-dd，如果需要其他模式，
	 * 请采用重载方法：fromObjctToJson(Object obj, String dateFmt)以便自行指定模式
	 * 
	 * @param obj	需要格式化的对象
	 * 
	 * @return 
	 */
	public static String fromObjctToJson(Object obj) {
		return CommonUtil.fromObjctToJson(obj, null);
	}
	
	/**
	 * 获取指定网址的网页html代码
	 * 
	 * @param url
	 * @param encoding
	 * @return
	 */
	public static String getURLContent(String url, String encoding) {
	  StringBuffer content = new StringBuffer();
	  try {
		   // 新建URL对象
		   URL u = new URL(url);
		   InputStream in = new BufferedInputStream(u.openStream());
		   InputStreamReader theHTML = new InputStreamReader(in, encoding);
		   int c;
		   while ((c = theHTML.read()) != -1) {
			   content.append((char)c);
		   }
	  }
	  // 处理异常
	  catch (MalformedURLException e) {
		  System.err.println(e);
	  } catch (IOException e) {
		  System.err.println(e);
	  }
	  return content.toString();
	}
	
	/**
	 * 将字符串转化成整数
	 * 
	 * @param str
	 * @return
	 */
	public static int StringToInt(String str) {
		int result = 0;
		if(null != str && !"".equals(str)) {
			try {
				result = Integer.parseInt(str);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
}
