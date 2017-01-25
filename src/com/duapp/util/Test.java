package com.duapp.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Test {

	public static void main(String[]sraga) throws IOException {
		String mgsy1 = "--";
		float mg = CommonUtil.StringToFloat(mgsy1);
		System.out.println(mg);
	    if (null == mgsy1 || "".equals(mgsy1) || CommonUtil.StringToFloat(mgsy1) <= 0) {
	    	mgsy1 = "0";
	    }
	    System.out.println(mgsy1);
		
		String icbhy="";
		String ltag="0";
		//上市时间
		String sssj = "0";
		String updateType = "";
		
		String url = "http://stockdata.stock.hexun.com/002394.shtml";
		//url = "http://www.baidu.com";
		String xmlDoc = CommonUtil.getURLContentByHttpClient(url, "gbk");
		System.out.println(xmlDoc);
		//ICB行业
		Pattern p = Pattern.compile("quote.hexun.com/stock/icb.aspx\\?code=.*?>(.*?)</a>");
	    Matcher m = p.matcher(xmlDoc);
	    if(m.find()) {
	    	icbhy = m.group(1);
	    }
	    //流通A股
	    p = Pattern.compile("stockdata.stock.hexun.com/gszl/jbzllinkPage.aspx\\?c=.*?>(.*?)</a>");
	    m = p.matcher(xmlDoc);
	    int i = 0;
	    while(m.find()) {
	    	if((i++)==1){
	    		ltag = m.group(1);
	    		break;
	    	}
	    }
	    if (null == ltag || "".equals(ltag)) {
	    	ltag = "0";
	    }
	    //上市时间
	    p = Pattern.compile("var debutDate = \"(.*?)\";");
	    m = p.matcher(xmlDoc);
	    if(m.find()) {
	    	sssj = m.group(1);
	    }
	    if (null != sssj && !"".equals(sssj)) {
	    	sssj = sssj.replace("-", "");
	    } else {
	    	sssj = "0";
	    }
	    
	  //每股收益(元)
		String mgsy = "0";
		p = Pattern.compile("<td width='70' class='tb2_new'>(.*?)</td>");
	    m = p.matcher(xmlDoc);
	    if(m.find()) {
	    	mgsy = m.group(1);
	    }
	    
	    System.out.println("ltag="+ltag+",sssj="+sssj+",mgsy="+mgsy);
	}
}
