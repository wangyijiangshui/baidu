package com.duapp.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jsoup.helper.StringUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;

/**
 * 从网站分页抓取股票信息（股票代码、价格、涨跌额、涨跌比例），更新到数据库中
 * 
 * @author	Administrator
 * @date	2018年11月11日
 */
public class StockPriceTask {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		StockPriceTask task = new StockPriceTask();
		task.grapDataFromWebSite();
		
	}
	
	/**
	 * 从网站分页抓取股票信息（股票代码、价格、涨跌额、涨跌比例），更新到数据库中
	 * 
	 */
	public void grapDataFromWebSite() {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			//沪市A股
			//http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=1&num=40&sort=symbol&asc=1&node=sh_a&symbol=&_s_r_a=page
			boolean result = true;
			int page = 1;
			String url = null;
			while (result) {
				url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page="+page+"&num=40&sort=symbol&asc=1&node=sh_a&symbol=&_s_r_a=page";
				result = this.saveOnePage(url, conn, pstmt);
				page = page + 1;
			}
			
			//深市A股
			//http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=51&num=40&sort=symbol&asc=1&node=sz_a&symbol=&_s_r_a=page
			result = true;
			page = 1;
			while (result) {
				url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page="+page+"&num=40&sort=symbol&asc=1&node=sz_a&symbol=&_s_r_a=page";
				result = this.saveOnePage(url, conn, pstmt);
				page = page + 1;
			}
		} catch(Exception ex) {
			DBUtil.close(pstmt, conn);
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 
	 * @param url
	 * @param conn
	 * @param pstmt
	 * @throws SQLException 
	 */
	public boolean saveOnePage(String url, Connection conn, PreparedStatement pstmt){
		try {
			System.out.println(url);
			String html = CommonUtil.getURLContentByHttpClient(url, "gbk");
			System.out.println(html);
			if (StringUtil.isBlank(html) || "null".equals(html)) {
				return false;
			}
			
			JSONArray jsonArray = JSONArray.parseArray(html);
			if(null == jsonArray || jsonArray.size() <= 0) {
				return false;
			}
			System.out.println(jsonArray.size());
			
			String sql = "update tbl_gp set gpjg=?, zde=?, zdbl=? where gpdm=?";
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false);  
			pstmt = conn.prepareStatement(sql);
			for (int i =0; i < jsonArray.size(); i++) {
				JSONObject obj = (JSONObject)jsonArray.get(i);
				System.out.println(obj.get("code")+","+obj.getString("name")+",gpjg="+obj.getString("trade")+",zde="+obj.getString("pricechange")+",zdbl="+obj.getString("changepercent"));
				
				int index = 1;
				pstmt.setString(index++, obj.getString("trade"));
				pstmt.setString(index++, obj.getString("pricechange"));
				pstmt.setString(index++, obj.getString("changepercent"));
				pstmt.setString(index++, obj.getString("code"));
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			conn.commit();
			pstmt.clearBatch();
			System.out.println("Success to save stock price info :"+jsonArray.size());
			Thread.sleep(1000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}
}
