package com.duapp.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;
import com.duapp.vo.StockRemark;
import com.duapp.vo.Message;

public class StockAction extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String method = request.getParameter("method");
		//修改股票价值权重
		if("changeWeight".equals(method)) {
			this.changeWeight(request, response);
		//刷新数据库股票数据
		} else if ("refreshDbButton".equals(method)){
			this.refreshDbButton(request, response);
		//刷新基础信息到数据库
		} else if ("refreshBaseButton".equals(method)){
			this.refreshBaseButton(request, response);
		//实时刷新界面行情数据
		} else if ("refreshPageButton".equals(method)) {
			this.refreshPageButton(request, response);
		//清空新提示消息(最近更新内容提示)
		} else if ("readNews".equals(method)) {
			this.readNews(request, response);
		//保存备注信息
		} else if ("writeRemark".equals(method)) {
			this.writeRemark(request, response);
		//加载指定股票的历史详细备注信息
		} else if ("loadHistoryRemark".equals(method)) {
			this.loadHistoryRemark(request, response);
		} else if ("writeRemarkTime".equals(method)) {
			this.writeRemarkTime(request, response);
		}
		
		
	}
	
	/**
	 * 实时刷新界面行情数据
	 * 代码	  名称  最新价  涨跌幅  昨收  今开  最高  最低  成交量  成交额  换手  振幅  量比
	 * 
	 * ['000687','恒天天鹅',4.93,10.04,4.48,4.93,4.93,4.89,538176.76,265310560,7.67,0.89,1.94]
	 * 
	 * @param request
	 * @param response
	 */
	public void refreshPageButton(HttpServletRequest request, HttpServletResponse response) {
		boolean result = false;
		String[] gpdms = null;
		Map<String, String> map = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String xmlDoc = null;
		String url = null;
		String[]  gpInfos = null;
		try {
			url = "http://quote.tool.hexun.com/hqzx/quote.aspx?type=2&market=2&sorttype=3&updown=up&page=1&count=2000&time=0";
			xmlDoc = CommonUtil.getURLContent(url, "gbk");
			xmlDoc = xmlDoc.substring(xmlDoc.indexOf("= ")+3, xmlDoc.indexOf("];")).trim();
			gpdms = xmlDoc.split("],");
			for (String gpdm : gpdms) {
				try {
					gpInfos = gpdm.trim().replace("[", "").replace("'", "").replace("]", "").split(",");
					
					map = new HashMap<String, String>();
					map.put("gpdm", gpInfos[0]);//搜索股票代码
					map.put("gsmc", gpInfos[1]);//搜索股票名称
					map.put("gpjg", gpInfos[2]);//搜索股票价格
					String zde = ((Float.parseFloat(gpInfos[2])*Float.parseFloat(gpInfos[3]))/100)+"";
					zde = zde.length() > 4 ? zde.substring(0,4) : zde;
					map.put("zde", zde);//搜索股票涨跌额
					map.put("zdbl", gpInfos[3]);//搜索股票涨跌比率
					list.add(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result, CommonUtil.fromObjctToJson(list))), response);
	}
	
	/**
	 * 刷新数据库股票数据
	 * 
	 * 代码	  名称  最新价  涨跌幅  昨收  今开  最高  最低  成交量  成交额  换手  振幅  量比
	 * 
	 * ['000687','恒天天鹅',4.93,10.04,4.48,4.93,4.93,4.89,538176.76,265310560,7.67,0.89,1.94]
	 * 
	 * @param request
	 * @param response
	 */
	public void refreshDbButton(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		Statement updateStmt = null;
		boolean result = false;
		String xmlDoc = null;
		String url = null;
		String[]  gpInfos = null;
		String[] gpdms = null;
		String sql = null;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			updateStmt = conn.createStatement();
			url = "http://quote.tool.hexun.com/hqzx/quote.aspx?type=2&market=2&sorttype=3&updown=up&page=1&count=2000&time=0";
			xmlDoc = CommonUtil.getURLContent(url, "gbk");
			xmlDoc = xmlDoc.substring(xmlDoc.indexOf("= ")+3, xmlDoc.indexOf("];")).trim();
			gpdms = xmlDoc.split("],");
			//先清空数据库中股票的价格信息数据
			sql = "update tbl_gp set gpjg=null,zde=null,zdbl=null";
			updateStmt.executeUpdate(sql);
			for (String gpdm : gpdms) {
				try {
					gpInfos = gpdm.trim().replace("[", "").replace("'", "").replace("]", "").split(",");
					//根据现价和涨跌幅度及时涨跌额度
					String zde = ((Float.parseFloat(gpInfos[2])*Float.parseFloat(gpInfos[3]))/100)+"";
					zde = zde.length() > 4 ? zde.substring(0,4) : zde;
					
					sql = "update tbl_gp set gsmc='"+gpInfos[1]+"',gpjg='"+gpInfos[2]+"',zde='"+zde+"',zdbl='"+gpInfos[3]+
							"' where gpdm='sz"+gpInfos[0]+"'";
					System.out.println("sql="+sql);
					//如果没有更新成功，则判断当前股票是否是深圳A股，如果是则保存
					if(updateStmt.executeUpdate(sql) <= 0 && gpInfos[0].startsWith("00")) {
						sql  = "INSERT INTO `tbl_gp`(`gpdm`,`gsmc`,`gpjzqz`,`gpjg`,`zde`,`zdbl`,`updateType`,`updateTypeTime`) " +
									"VALUES ('sz"+gpInfos[0]+"','"+gpInfos[1]+"',-1,'"+gpInfos[2]+"','"+zde+"','"+gpInfos[3]+"','NEW!!',now())";
						updateStmt.executeUpdate(sql);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(stmt, conn);
			DBUtil.close(updateStmt, null);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 刷新数据库股票的基础信息
	 * 
	 * 
	 * @param request
	 * @param response
	 */
	public void refreshBaseButton(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		Statement updateStmt = null;
		String sql = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			updateStmt = conn.createStatement();
			sql = "SELECT gpdm,icbhy,ltag FROM tbl_gp";
			rs = stmt.executeQuery(sql);
			String gpdm = null;
			String xmlDoc = null;
			String url = null;
			
			String icbhy="";
			String ltag="";
			String updateType = "";
			
			while(rs.next()) {
				updateType = "";
				icbhy="";
				ltag="";
				try {
					gpdm = rs.getString("gpdm");
					if (null != gpdm && !"".equals(gpdm)) {
						url = "http://stockdata.stock.hexun.com/"+(gpdm.replace("sz", ""))+".shtml";
						xmlDoc = CommonUtil.getURLContent(url, "gbk");
						
						Pattern p = Pattern.compile("quote.hexun.com/stock/icb.aspx\\?code=.*?>(.*?)</a>");
					    Matcher m = p.matcher(xmlDoc);
					    if(m.find()) {
					    	icbhy = m.group(1);
					    }
					    p = Pattern.compile("stockdata.stock.hexun.com/gszl/jbzllinkPage.aspx\\?c=.*?>(.*?)</a>");
					    m = p.matcher(xmlDoc);
					    int i = 0;
					    while(m.find()) {
					    	if((i++)==1){
					    		ltag = m.group(1);
					    		break;
					    	}
					    }
					    //如果本次更新的内容与数据库中保存的不一致，则记录本次新的内容
					    if (null != icbhy && !icbhy.equals(rs.getString("icbhy")) && null != rs.getString("icbhy")) {
					    	updateType = rs.getString("icbhy") + "->" + icbhy;
					    }
					    if (null != ltag && !ltag.equals(rs.getString("ltag")) && null != rs.getString("ltag")) {
					    	updateType = updateType+(!"".equals(updateType) ? ",":"")+rs.getString("ltag") + "->" + ltag;
					    }
					    
					    if (null != updateType && !"".equals(updateType)) {
					    	updateStmt.executeUpdate("update tbl_gp set icbhy='"+icbhy+"',ltag='"+ltag+"',updateType='"+updateType+"',updateTypeTime=now() where gpdm='"+gpdm+"'");
					    } else {
					    	updateStmt.executeUpdate("update tbl_gp set icbhy='"+icbhy+"',ltag='"+ltag+"' where gpdm='"+gpdm+"'");
					    }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
			DBUtil.close(updateStmt, null);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	public static void main(String[]args) {
		String xmlDoc = "<img src=\"fdsfs.jpg\"/>";
		Pattern p = Pattern.compile("src=\"(.*?)\"/>");
	    Matcher m = p.matcher(xmlDoc);
	    String icbhy = null;
	    if(m.find()) {
	    	icbhy = m.group(1);
	    }
	    System.out.println(icbhy);
	}
	
	/**
	 * 修改股票价值权重
	 * 
	 * @param request
	 * @param response
	 */
	public void changeWeight(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		String sql = null;
		String gpjzqz = request.getParameter("gpjzqz");
		String id = request.getParameter("id");
		String remark = request.getParameter("remark");
		boolean result = false;
		if (null != gpjzqz && null != id) {
			try {
				conn = DBUtil.getConnection();
				stmt = conn.createStatement();
				if(null != remark && !"".equals(remark)) {
					sql = "update tbl_gp set gpjzqz="+gpjzqz+",remark='"+remark+"',remarkTime=now() where id="+id;
				} else {
					sql = "update tbl_gp set gpjzqz="+gpjzqz+" where id="+id;
				}
				if(stmt.executeUpdate(sql) > 0) {
					if(null != remark && !"".equals(remark)) {
						sql = "INSERT INTO `tbl_gp_remark`(gpdm,remark,createTime) SELECT gpdm,remark,NOW() FROM `tbl_gp` where id="+id;
						if(stmt.executeUpdate(sql) > 0) {
							result = true;
						}
					} else {
						result = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(stmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 清空新提示消息(最近更新内容提示)
	 * 
	 * @param request
	 * @param response
	 */
	public void readNews(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		String sql = null;
		String id = request.getParameter("id");
		boolean result = false;
		if (null != id) {
			try {
				conn = DBUtil.getConnection();
				stmt = conn.createStatement();
				sql = "update tbl_gp set updateType='' where id="+id;
				if(stmt.executeUpdate(sql) > 0) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(stmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 任务状态+备注修改
	 * 
	 * @param request
	 * @param response
	 */
	public void writeRemark(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		String sql = null;
		String remark = request.getParameter("remark");
		String gpdm = request.getParameter("gpdm");
		boolean result = false;
		if (null != gpdm) {
			try {
				conn = DBUtil.getConnection();
				stmt = conn.createStatement();
				sql = "update tbl_gp set remark='"+remark+"',remarkTime=now() where gpdm='"+gpdm+"'";
				if(stmt.executeUpdate(sql) > 0) {
					sql = "insert into `tbl_gp_remark`(`gpdm`,`remark`,`createTime`) VALUES('"+gpdm+"','"+remark+"',now()) ;";
					if(stmt.executeUpdate(sql) > 0) {
						result = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(stmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 加载指定股票的历史详细备注信息
	 * 
	 * @param request
	 * @param response
	 */
	public void loadHistoryRemark(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<StockRemark> remarks = new ArrayList<StockRemark>();
		StockRemark remark = null;
		String gpdm = request.getParameter("gpdm");
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT remark,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i') createTime  FROM `tbl_gp_remark` where gpdm='"+gpdm+"' order by createTime desc";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				remark = new StockRemark();
				remark.setRemark(rs.getString("remark"));
				remark.setCreateTime(rs.getString("createTime"));
				remarks.add(remark);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(remarks), response);
	}
	
	/**
	 * 更新股票最近查看时间
	 * 
	 * @param request
	 * @param response
	 */
	public void writeRemarkTime(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		String sql = null;
		String gpdm = request.getParameter("gpdm");
		boolean result = false;
		if (null != gpdm) {
			try {
				conn = DBUtil.getConnection();
				stmt = conn.createStatement();
				sql = "update tbl_gp set remarkTime=now() where gpdm='"+gpdm+"'";
				if(stmt.executeUpdate(sql) > 0) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(stmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
}
