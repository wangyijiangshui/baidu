package com.duapp.dao.network;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;
import com.duapp.util.FileUtil;

/**
 * 交易明细
 * 
 * @author Administrator
 *
 */
public class JiaoYiDetailDao {

	private Logger logger = Logger.getLogger(JiaoYiDetailDao.class);      
	
	public static void main(String[] args) {
		JiaoYiDetailDao jiaoYiDetailDao = new JiaoYiDetailDao();
		jiaoYiDetailDao.loadJiaoYiDetail();
	}
	
	/**
	 * 下载交易明细数据
	 */
	public void loadJiaoYiDetail() {
		String year = CommonUtil.formatDate(new Date(), "yyyy");
		String todayStr = null;
		
		List<GpInfo> gpInfos = this.getAllGpdm();
		for (GpInfo info : gpInfos) {
			File file = new File("f:/" + year + "/" + info.getGpdm() + "_" + info.getGsmc());
			if(!file.exists()) {
				file.mkdirs();
			}
			//开始下载交易数据xls文件
			Calendar today = Calendar.getInstance();
			int failSum = 0;
			while (true) {
				today.add(Calendar.DAY_OF_MONTH, -1);
				todayStr = CommonUtil.formatCalendar(today, "yyyyMMdd");
				
				File newFile = new File (file, info.getGpdm() + "_" + info.getGsmc() +"_" + todayStr + ".xls");
				String url = "http://stock.gtimg.cn/data/index.php?appn=detail&action=download&c=sz"+info.getGpdm()+"&d="+todayStr;
				boolean result = FileUtil.downloadNet(url, newFile, 512);
				logger.info(info.getGpdm() + "_" + info.getGsmc() +"_" + todayStr + " : " + result);
				
				if(!result) {
					failSum = failSum + 1;
				} else {
					failSum = 0;
				}
				if(failSum > 10 || "20150101".equals(todayStr)) {
					break;
				}
			}
			//等两秒
			try {
				logger.info("waiting 2 seconds ......");
				Thread.sleep(2 * 1000);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<GpInfo> getAllGpdm() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<GpInfo> gpInfos = new ArrayList<GpInfo>();
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT gpdm,gsmc from tbl_gp where gpjzqz>=8 order by gpjzqz desc";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				GpInfo gpInfo = new GpInfo();
				gpInfo.setGpdm(rs.getString("gpdm"));
				gpInfo.setGsmc(rs.getString("gsmc"));
				gpInfos.add(gpInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		return gpInfos;
	}
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	private class GpInfo{
		private String gpdm;
		private String gsmc;
		public String getGpdm() {
			return gpdm;
		}
		public void setGpdm(String gpdm) {
			this.gpdm = gpdm;
		}
		public String getGsmc() {
			return gsmc;
		}
		public void setGsmc(String gsmc) {
			this.gsmc = gsmc;
		}
	}
}
