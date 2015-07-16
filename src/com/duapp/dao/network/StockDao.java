package com.duapp.dao.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.duapp.util.CommonUtil;

/**
 * 
 * 
 * 
 * @author Administrator
 *
 */
public class StockDao {

	/**简易获取指定股票和大盘数据的接口*/
	//http://quote.stock.hexun.com/stockdata/stock_quote.aspx?stocklist=000651&indexlist=000001_1|399001_2&time=151820
	
	/**
	 * 从和讯网抓取市盈率
	 * 
	 * http://webstock.quote.hermes.hexun.com/a/quotelist?code=szse000651&callback=callback&column=
	 * 			DateTime,LastClose,Open,High,Low,Price,Volume,Amount,LastSettle,SettlePrice,OpenPosition,ClosePosition,BuyPrice,
	 * 			BuyVolume,SellPrice,SellVolume,PriceWeight,EntrustRatio,UpDown,EntrustDiff,UpDownRate,OutVolume,InVolume,AvePrice,VolumeRatio,
	 * 			PE,ExchangeRatio,LastVolume,VibrationRatio,DateTime,OpenTime,CloseTime
	 * 
	 * 
	 * 其中code=
	 * 		szse000651：深市
	 * 		sse600321:沪市
	 * 
	 * column=
	 * 		DateTime：数据时间
	 * 		PE:市盈率
	 * 
	 * 
	 * @return
	 */
	public static float getStockJtsylFromHexun(String gpdm) {
		float jtsyl = 0;
		String url = "http://webstock.quote.hermes.hexun.com/a/quotelist?code=#gpdm#&column=PE";
		if(null == gpdm || "".equals(gpdm)) {
			return 0;
		}
		if(gpdm.startsWith("00")){
			gpdm = "szse"+gpdm;
		} else {
			gpdm = "sse"+gpdm;
		}
		//({"Data":[[[1542]]]});
		try {
			String xmlDoc = CommonUtil.getURLContent(url.replace("#gpdm#", gpdm), "gbk");
			if(null != xmlDoc) {
				xmlDoc = xmlDoc.replace("({\"Data\":[[[", "").replace("]]]});", "");
			}
			jtsyl = (Float.parseFloat(xmlDoc)/100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jtsyl;
	}
	
	/**
	 * 获取股票上市时间
	 * 
	 * http://stockdata.stock.hexun.com/000651.shtml
	 * 
	 * @param gpdm
	 * @return
	 */
	public static String getStockSssj(String gpdm) {
		String sssj = "0";
		try {
			String xmlDoc = CommonUtil.getURLContentByHttpClient("http://stockdata.stock.hexun.com/"+gpdm+".shtml", "gbk");
			//上市时间
			Pattern p = Pattern.compile("var debutDate = \"(.*?)\";");
			Matcher m = p.matcher(xmlDoc);
			if(m.find()) {
				sssj = m.group(1);
			}
			if (null != sssj && !"".equals(sssj)) {
				sssj = sssj.replace("-", "");
			} else {
				sssj = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sssj;
	}
	
	public static void main(String[]args){
		String a = StockDao.getStockSssj("002142");
		String b = "";
	}
}
