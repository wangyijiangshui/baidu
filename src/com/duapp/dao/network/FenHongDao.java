package com.duapp.dao.network;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;

/**
 * 股票分红数据提取
 * 
 * @author Administrator
 * 
 */
public class FenHongDao {

	public static void main(String[] args) {
		FenHongDao fenHongDao = new FenHongDao();
		fenHongDao.findAllFenhongInfoAndSave();
	}

	public void findAllFenhongInfoAndSave() {
		List<String> gpdms = this.getAllGpdm();
		String url = "http://stockdata.stock.hexun.com/2009_bgqfpya_gpdm.shtml";
		
		// 先清空表数据
		this.clearTable();
		// 抓取所有股票的分红信息并保存
		int num = 0;
		for(String gpdm : gpdms) {
			num = num + 1;
			System.out.println("\n\n"+num+"：gpdm="+gpdm+"==>");
			try {
				String html = CommonUtil.getURLContentByHttpClient(url.replace("gpdm", gpdm), "gbk");
				Document doc = Jsoup.parse(html);
				Elements elements = doc.getElementsByClass("tishi");
				
				for(Element ele : elements) {
					String fenHongContent = ele.text();
					if(null != fenHongContent && fenHongContent.contains("扣税后")) {
						int year = CommonUtil.StringToInt(fenHongContent.substring(0, 4));
						if(year <= 0) {
							year = CommonUtil.StringToInt("20"+fenHongContent.substring(0, 2));
						}
						float fenHong = 0;
						int index = fenHongContent.indexOf("扣税后10派");
						if(index >= 0) {
							fenHong = CommonUtil.StringToFloat(fenHongContent.substring(index).replace("元)", "").replace("扣税后10派", ""));
						}
						System.out.println("fenHongContent="+fenHongContent+",year="+year+",fenHong="+fenHong);
						this.saveKlineData(gpdm, year, fenHong, fenHongContent);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 批量计算100元钱对应的分红金额
		this.updateFenhong100();
		DBUtil.close(null, conn);
	}

	/**
	 * 先清空表的所有记录
	 * 
	 * @return
	 */
	public boolean clearTable() {
		System.out.println("Clear table .....");
		boolean result = false;
		String sql = "delete from tbl_gp_fenhong";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, null);
		}
		return result;
	}

	/**
	 * 保存一条股票的分红红利
	 * 
	 */
	private Connection conn = DBUtil.getConnection();

	public boolean saveKlineData(String gpdm, int year, float fenhong,
			String fenHoneContent) {
		boolean result = false;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO tbl_gp_fenhong(gpdm,year,fenHong,fenHongContent,createTime) values (?,?,?,?,now());";
		try {
			pstmt = conn.prepareStatement(sql);

			int index = 1;
			pstmt.setString(index++, gpdm);
			pstmt.setInt(index++, year);
			pstmt.setFloat(index++, fenhong);
			pstmt.setString(index++, fenHoneContent);
			pstmt.executeUpdate();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, null);
		}
		return result;
	}

	/**
	 * 根据当前的股价计算，如果是100元一股，那么花100元能够分到多少红利
	 * 
	 * @return
	 */
	public boolean updateFenhong100() {
		System.out.println("updateFenhong100......");
		boolean result = false;
		String sql = "UPDATE tbl_gp_fenhong INNER JOIN tbl_gp ON tbl_gp_fenhong.gpdm=tbl_gp.gpdm SET fenHong100=(fenHong*100)/gpjg";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, null);
		}
		return result;
	}

	/**
	 * 获取所有的股票代码
	 * 
	 * @return
	 */
	public List<String> getAllGpdm() {
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<String> gpdms = new ArrayList<String>();
		try {
			stmt = conn.createStatement();
			sql = "SELECT gpdm from tbl_gp";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				gpdms.add(rs.getString("gpdm"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, null);
		}
		// 默认加上参照物：贵州茅台
		gpdms.add("600519");
		return gpdms;
	}
}
