package com.duapp.util;

import java.text.SimpleDateFormat;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * [Java -> JSON] java.util.Date -> "yyyy-MM-dd" etc. string <br/>
 * 
 * Json-lib默认的Java -> JSON的日期表示格式与java经常用的大相径庭，所以使用此
 * 扩展来将Date格式化为字符串。
 * 
 * 
 * @author js
 * @date	2013-05-07
 */
public class DateJsonValueProcessor implements JsonValueProcessor{

	/**日期格式化模式*/
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";

	public DateJsonValueProcessor() {
		
	}
	
	public DateJsonValueProcessor(String dateFmt) {
		this.dateFormat = dateFmt;
	}
	
	@Override
    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        return null;
    }

	@Override
    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        if (null != value) {
        	if (value instanceof java.util.Date) {
        		return sdf.format(value);
            }else {
            	return value.toString();
            }
        }else{
        	return "";
        }
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
