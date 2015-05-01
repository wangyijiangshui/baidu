package com.duapp.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;
import com.duapp.vo.StockRemark;

public class DayKlineDataDao {

	public static void main(String[]args) throws IOException {
		String url = "http://d.10jqka.com.cn/v2/line/hs_000651/01/today.js";
		url = "http://d.10jqka.com.cn/v2/line/hs_000651/01/1991.js";
		//url = "http://d.10jqka.com.cn/v2/line/hs_000651/01/2014.js";
		//Document doc = Jsoup.connect(url).get();
		String klineDataStr = CommonUtil.getURLContent(url, "gbk");
		System.out.println(klineDataStr);
		klineDataStr = klineDataStr.replaceFirst(".+\\(\\{\"data\":\"", "").replaceFirst("\"}\\)", "");
		System.out.println(klineDataStr);
	}

	public List<String> getAllGpdm() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<String> gpdms = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT gpdm from tbl_gp";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				gpdms.add(rs.getString("gpdm").replace("sz", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		return gpdms;
	}
	
}
