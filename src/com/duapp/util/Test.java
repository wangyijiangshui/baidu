package com.duapp.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Test {

	public static void main(String[]sraga) throws IOException {
		String url = "http://stockdata.stock.hexun.com/000651.shtml";
		Document doc = Jsoup.connect(url).get();
		System.out.println(doc.toString());
		Elements baseInfoTable = doc.select(".box6");
		//上市时间
		String sssj = baseInfoTable.select("tr").eq(1).select("td").eq(2).select("div").html();
		//流通A股(亿)
		String ltag = null;
		//ICB行业
		String icbhy = null;
		
		//System.out.println("sssj="+sssj+",ltag="+ltag+",icbhy="+icbhy);
	}
}
