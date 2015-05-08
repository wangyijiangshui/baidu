package com.duapp.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.duapp.pojo.GpKlinePojo;
import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;

/**
 * 抓取股票的日K线数据
 * 
 * @author Administrator
 *
 */
public class DayKlineDataDao {
	/***/
	private Logger logger = Logger.getLogger(DayKlineDataDao.class);      
	
	public static void main(String[]args) throws IOException {
		DayKlineDataDao dayKlineDataDao = new DayKlineDataDao();
		List<GpInfo> gpInfos = dayKlineDataDao.getAllGpdm();
		dayKlineDataDao.spiderForKlineFromThsWebsit(gpInfos, dayKlineDataDao);
	}

	private static String klineDataUrl = "http://d.10jqka.com.cn/v2/line/hs_gpdm/01/yyyy.js";
	//抓取股票K数据的截止年份（今年）
	private static int endYear = CommonUtil.StringToInt(CommonUtil.formatDate(new Date(), "yyyy"));
	//抓取股票K数据的起始年份（2000年）
	private static int spiderKlineStartYear = 2000; 
	public void spiderForKlineFromThsWebsit(List<GpInfo> gpInfos, DayKlineDataDao dayKlineDataDao) {
		int index = 0;
		for (GpInfo gpInfo : gpInfos) {
			index = index + 1;
			try {
				logger.info(index+":"+gpInfo.getGpdm()+"->");
				List<GpKlinePojo> gpKlinePojos = null;
				
				for (int year = endYear; year >= spiderKlineStartYear; year--) {
					logger.info(year+":");
					String klineDataStr = CommonUtil.getURLContent(DayKlineDataDao.klineDataUrl.replace("gpdm", gpInfo.getGpdm()).replace("yyyy", year+""), "gbk");
					logger.info(",");
					klineDataStr = klineDataStr.replaceFirst(".+\\(\\{\"data\":\"", "").replaceFirst("\"}\\)", "");
					if(null == klineDataStr || "".equals(klineDataStr)) {
						continue;
					}
					logger.info(",");
					if(null == gpKlinePojos) {
						gpKlinePojos = this.parseKlineData(gpInfo.getGpdm(), klineDataStr, 1);
					} else {
						gpKlinePojos.addAll(this.parseKlineData(gpInfo.getGpdm(), klineDataStr, 1));
					}
					logger.info(",");
					//当前就小于等于上市时间，退出本股票K数据的抓取
					if (year <= gpInfo.getSssj()) {
						break;
					}
				}
				logger.info("saving...");
				this.saveKlineData(gpKlinePojos);
				logger.info("end\n");
				Thread.sleep((1 * 1000));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public List<GpKlinePojo> parseKlineData(String gpdm, String klineDataStr, int klineType) {
		List<GpKlinePojo> gpKlinePojos = new ArrayList<GpKlinePojo>();
		GpKlinePojo gpKlinePojo = null;
		
		if(null != klineDataStr && !"".equals(klineDataStr)) {
			String[] klineDataArr = klineDataStr.split(";");
			for (String klineData : klineDataArr) {
				String[] klineTemp = klineData.split(",");
				if (null != klineTemp && klineTemp.length >= 7) {
					gpKlinePojo = new GpKlinePojo();
					gpKlinePojo.setGpdm(gpdm);
					gpKlinePojo.setKlineDay(CommonUtil.StringToInt(klineTemp[0]));
					gpKlinePojo.setKpPrice(CommonUtil.StringToFloat(klineTemp[1]));
					gpKlinePojo.setZgPrice(CommonUtil.StringToFloat(klineTemp[2]));
					gpKlinePojo.setZdPrice(CommonUtil.StringToFloat(klineTemp[3]));
					gpKlinePojo.setSpPrice(CommonUtil.StringToFloat(klineTemp[4]));
					gpKlinePojo.setCjl(CommonUtil.StringToFloat(klineTemp[5]));
					gpKlinePojo.setCje(CommonUtil.StringToFloat(klineTemp[6]));
					gpKlinePojo.setKlineType(klineType);
					gpKlinePojos.add(gpKlinePojo);
				}
			}
		}
		return gpKlinePojos;
	}
	
	private static Connection conn = DBUtil.getConnection();
	public boolean saveKlineData(List<GpKlinePojo> gpKlinePojos) {
		boolean result  = false;
		int gpdmInt = 0;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `tbl_gp_kline`(`gpdm`,`gpdmInt`,`klineDay`,`kpPrice`,`zgPrice`,`zdPrice`,`spPrice`,`zdf`," +
						"`cjl`,`cje`,`klineType`,`createTime`) VALUES (?,?,?,?,?,?,?,?,?,?,?,now())";
		try {
			conn.setAutoCommit(false);  
			pstmt = conn.prepareStatement(sql);
			float preDaySpPrice = 0;
			for (GpKlinePojo gpKline : gpKlinePojos) {
				//计算涨跌幅度(今日收盘价-前日收盘价/前日收盘价)，没有前一日的，则取今日开盘价
				if(0 == preDaySpPrice){
					preDaySpPrice = gpKline.getKpPrice();
				}
				gpKline.setZdf((gpKline.getSpPrice()-preDaySpPrice)/(0==preDaySpPrice ? 1:preDaySpPrice));
				preDaySpPrice = gpKline.getSpPrice();
				
				int index = 1;
				pstmt.setString(index++, gpKline.getGpdm());
				pstmt.setInt(index++, gpKline.getGpdmInt());
				pstmt.setInt(index++, gpKline.getKlineDay());
				pstmt.setFloat(index++, gpKline.getKpPrice());
				pstmt.setFloat(index++, gpKline.getZgPrice());
				pstmt.setFloat(index++, gpKline.getZdPrice());
				pstmt.setFloat(index++, gpKline.getSpPrice());
				pstmt.setFloat(index++, gpKline.getZdf());
				pstmt.setFloat(index++, gpKline.getCjl());
				pstmt.setFloat(index++, gpKline.getCje());
				pstmt.setFloat(index++, gpKline.getKlineType());
				pstmt.addBatch();
				gpdmInt = gpKline.getGpdmInt();
				//System.out.println(gpKline);
			}
			pstmt.executeBatch();
			conn.commit();
			pstmt.clearBatch();
			
			sql = "update tbl_gp set haveDayKline=1 where gpdmInt="+gpdmInt;
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			conn.commit();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, null);
		}
		return result;
	}
	
	public List<GpInfo> getAllGpdm() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<GpInfo> gpInfos = new ArrayList<GpInfo>();
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT gpdm,sssj from tbl_gp where haveDayKline=0";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				GpInfo gpInfo = new GpInfo();
				gpInfo.setGpdm(rs.getString("gpdm").replace("sz", ""));
				gpInfo.setSssj(rs.getInt("sssj")/10000);
				gpInfos.add(gpInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		System.out.println("gpInfos="+gpInfos);
		return gpInfos;
	}
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	private class GpInfo{
		private String gpdm;
		private int sssj;
		public String getGpdm() {
			return gpdm;
		}
		public void setGpdm(String gpdm) {
			this.gpdm = gpdm;
		}
		public int getSssj() {
			return sssj;
		}
		public void setSssj(int sssj) {
			this.sssj = sssj;
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.gpdm+" "+this.sssj;
		}
	}
}
