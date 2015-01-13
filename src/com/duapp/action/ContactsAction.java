package com.duapp.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;
import com.duapp.util.FileUtil;
import com.duapp.vo.Contacts;
import com.duapp.vo.ContactsPhoto;
import com.duapp.vo.ContactsRemark;
import com.duapp.vo.ContactsType;
import com.duapp.vo.Message;

public class ContactsAction extends HttpServlet {

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
		//修改系统主题
		if("queryCatType".equals(method)) {
			this.queryCatType(request, response);
		//新增联系人
		} else if("addContactor".equals(method)) {
			this.addContactor(request, response);	
		//根据主键查找联系人信息，用于修改
		} else if ("queryById".equals(method)){
			this.queryById(request, response);	
		//根据主键值修改
		} else if("editContactor".equals(method)) {
			this.editContactor(request, response);	
		//加减权重
		} else if ("addOrMinusWeight".equals(method)) {
			this.addOrMinusWeight(request, response);
		//加载指定联系人的历史详细备注信息
		} else if ("loadEditHistory".equals(method)) {
			this.loadEditHistory(request, response);
		//发布新添加的照片
		} else if ("publishPhoto".equals(method)) {
			this.publishPhoto(request, response);
		//发布新添加的照片
		} else if ("loadPhotos".equals(method)) {
			this.loadPhotos(request, response);
		}
		
	}

	/**
	 * 查询所有的联系人“分类”
	 * 
	 * @param request
	 * @param response
	 */
	public void queryCatType(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<ContactsType> contactsTypes = new ArrayList<ContactsType>();
		ContactsType contactsType = null;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT id,typeName FROM tbl_contacts_type ORDER BY id DESC";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				contactsType = new ContactsType();
				contactsType.setId(rs.getString("id"));
				contactsType.setTypeName(rs.getString("typeName"));
				contactsTypes.add(contactsType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(contactsTypes), response);
	}
	
	/**
	 * 新增任务
	 * 
	 * @param request
	 * @param response
	 */
	public void addContactor(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		String logo = request.getParameter("logo");
		String name = request.getParameter("name");
		String sex = request.getParameter("sex");
		String telephone = request.getParameter("telephone");
		String qq = request.getParameter("qq");
		String email = request.getParameter("email");
		String bornProvince = request.getParameter("bornProvince");
		String workTitle = request.getParameter("workTitle");
		String school = request.getParameter("school");
		String homeAddress = request.getParameter("homeAddress");
		String workAddress = request.getParameter("workAddress");
		String knowAddress = request.getParameter("knowAddress");
		String knowTime = request.getParameter("knowTime");
		String birthday = request.getParameter("birthday");
		String call = request.getParameter("call");
		String weight = request.getParameter("weight");
		String catType = request.getParameter("catType");
		String remark = request.getParameter("remark");
		
		boolean result = false;
		if (null != name && !"".equals(name)) {
			try {
				conn = DBUtil.getConnection();
				sql = "INSERT INTO `tbl_contacts`" +
							"(`logo`,`name`,sex,`telephone`,`qq`,`email`,`bornProvince`,workTitle,school,`homeAddress`," +
							"`workAddress`,`knowAddress`,knowTime,`birthday`,`call`,`weight`,`catType`,`remark`,`createTime`)" +
					   "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()); ";
				pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, logo);
				pstmt.setString(2, name);
				pstmt.setString(3, sex);
				pstmt.setString(4, telephone);
				pstmt.setString(5, qq);
				pstmt.setString(6, email);
				pstmt.setString(7, bornProvince);
				
				pstmt.setString(8, workTitle);
				pstmt.setString(9, school);
				
				pstmt.setString(10, homeAddress);
				pstmt.setString(11, workAddress);
				pstmt.setString(12, knowAddress);
				pstmt.setString(13, knowTime);
				pstmt.setString(14, birthday);
				pstmt.setString(15, call);
				pstmt.setInt(16, CommonUtil.StringToInt(weight));
				pstmt.setInt(17, CommonUtil.StringToInt(catType));
				pstmt.setString(18, remark);
				if(pstmt.executeUpdate() > 0) {
					result = true;
					//转移图片
					if(null != logo && !"".equals(logo)) {
						rs = pstmt.getGeneratedKeys();
						int id = 0;
						if(rs.next()) {
							id = rs.getInt(1);
						}
						if(id > 0) {
							FileUtil.cutFile(UploadAction.filePath+"/"+logo, UploadAction.filePath+"/"+id, logo);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(rs, pstmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 根据主键查找联系人信息，用于修改
	 * 
	 * @param request
	 * @param response
	 */
	public void queryById(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		Contacts contacts = null;
		
		String id = request.getParameter("id");
		
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT  a.`id`,`logo`,`name`,`sex`,`telephone`,`qq`,`email`,`bornProvince`,`workTitle`,`school`," +
					"`homeAddress`,`workAddress`,`knowAddress`,knowTime,`birthday`,`call`,`weight`,`catType`,a.`remark`,b.typeName," +
					"date_format(a.createTime,'%Y-%m-%d') createTime FROM `tbl_contacts` a left outer join tbl_contacts_type b on a.catType=b.id WHERE a.id=" + id;
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				contacts = new Contacts();
				contacts.setId(rs.getString("id"));
				contacts.setLogo(rs.getString("logo"));
				contacts.setName(rs.getString("name"));
				contacts.setSex(rs.getString("sex"));
				contacts.setTelephone(rs.getString("telephone"));
				contacts.setQq(rs.getString("qq"));
				contacts.setEmail(rs.getString("email"));
				contacts.setBornProvince(rs.getString("bornProvince"));
				contacts.setWorkTitle(rs.getString("workTitle"));
				contacts.setSchool(rs.getString("school"));
				contacts.setHomeAddress(rs.getString("homeAddress"));
				contacts.setWorkAddress(rs.getString("workAddress"));
				contacts.setKnowAddress(rs.getString("knowAddress"));
				contacts.setKnowTime(rs.getString("knowTime"));
				contacts.setBirthday(rs.getString("birthday"));
				contacts.setCall(rs.getString("call"));
				contacts.setWeight(rs.getString("weight"));
				contacts.setCatType(rs.getString("catType"));
				contacts.setRemark(rs.getString("remark"));
				contacts.setTypeName(rs.getString("typeName"));
				contacts.setCreateTime(rs.getString("createTime"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(contacts), response);
	}
	
	/**
	 * 根据主键值修改
	 * 
	 * @param request
	 * @param response
	 */
	public void editContactor(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		String id = request.getParameter("id");
		String logo = request.getParameter("logo");
		String name = request.getParameter("name");
		String sex = request.getParameter("sex");
		String telephone = request.getParameter("telephone");
		String qq = request.getParameter("qq");
		String email = request.getParameter("email");
		String bornProvince = request.getParameter("bornProvince");
		String workTitle = request.getParameter("workTitle");
		String school = request.getParameter("school");
		String homeAddress = request.getParameter("homeAddress");
		String workAddress = request.getParameter("workAddress");
		String knowAddress = request.getParameter("knowAddress");
		String knowTime = request.getParameter("knowTime");
		String birthday = request.getParameter("birthday");
		String call = request.getParameter("call");
		String weight = request.getParameter("weight");
		String catType = request.getParameter("catType");
		String remark = request.getParameter("remark");
		
		String changes = request.getParameter("changes");
		
		boolean result = false;
		if (null != name && !"".equals(name)) {
			try {
				conn = DBUtil.getConnection();
				sql = "update `tbl_contacts`" +
							" set `logo`=?,`name`=?,sex=?,`telephone`=?,`qq`=?,`email`=?,`bornProvince`=?,workTitle=?," +
							" school=?,`homeAddress`=?,`workAddress`=?,`knowAddress`=?,knowTime=?,`birthday`=?,`call`=?,`weight`=?," +
							"`catType`=?,`remark`=? where id=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, logo);
				pstmt.setString(2, name);
				pstmt.setString(3, sex);
				pstmt.setString(4, telephone);
				pstmt.setString(5, qq);
				pstmt.setString(6, email);
				pstmt.setString(7, bornProvince);
				
				pstmt.setString(8, workTitle);
				pstmt.setString(9, school);
				
				pstmt.setString(10, homeAddress);
				pstmt.setString(11, workAddress);
				pstmt.setString(12, knowAddress);
				pstmt.setString(13, knowTime);
				pstmt.setString(14, birthday);
				pstmt.setString(15, call);
				pstmt.setInt(16, CommonUtil.StringToInt(weight));
				pstmt.setInt(17, CommonUtil.StringToInt(catType));
				pstmt.setString(18, remark);
				pstmt.setString(19, id);
				
				if(pstmt.executeUpdate() > 0) {
					//转移图片
					if(null != logo && !"".equals(logo)) {
						if (!FileUtil.isFileExist(UploadAction.filePath+"/"+id+"/"+logo)) {
							FileUtil.cutFile(UploadAction.filePath+"/"+logo, UploadAction.filePath+"/"+id, logo);
						}
					}
					//保存此处更改的内容
					if (null != changes && !"".equals(changes)) {
						sql = "insert into `tbl_contacts_remark`(`catId`,`remark`,`createTime`) VALUES("+id+",?,now()) ;";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, changes);
						if(pstmt.executeUpdate() > 0) {
							result = true;
						}
					} else {
						result = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(pstmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 加减权重
	 * 
	 * @param request
	 * @param response
	 */
	public void addOrMinusWeight(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		String id = request.getParameter("id");
		String weight = request.getParameter("weight");
		
		boolean result = false;
		if (null != weight && !"".equals(weight)) {
			try {
				conn = DBUtil.getConnection();
				sql = "update `tbl_contacts` set `weight`=? where id=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, CommonUtil.StringToInt(weight));
				pstmt.setString(2, id);
				
				if(pstmt.executeUpdate() > 0) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(pstmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 加载指定联系人的历史详细备注信息
	 * 
	 * @param request
	 * @param response
	 */
	public void loadEditHistory(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<ContactsRemark> remarks = new ArrayList<ContactsRemark>();
		ContactsRemark remark = null;
		String id = request.getParameter("id");
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT remark,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i') createTime  FROM `tbl_contacts_remark` WHERE catId="+id+" ORDER BY createTime DESC";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				remark = new ContactsRemark();
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
	 * 新增任务
	 * 
	 * @param request
	 * @param response
	 */
	public void publishPhoto(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		String catId = request.getParameter("catId");
		String photo = request.getParameter("photo");
		String remark = request.getParameter("remark");
		
		boolean result = false;
		if (null != photo && !"".equals(photo)) {
			try {
				conn = DBUtil.getConnection();
				sql = "INSERT INTO `tbl_contacts_photo`(`catId`,`photo`,remark,`createTime`) VALUES (?,?,?,now()); ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, CommonUtil.StringToInt(catId));
				pstmt.setString(2, photo);
				pstmt.setString(3, remark);
				if(pstmt.executeUpdate() > 0) {
					result = true;
					//转移图片
					FileUtil.cutFile(UploadAction.filePath+"/"+photo, UploadAction.filePath+"/"+catId, photo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(rs, pstmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 加载照片信息
	 * 
	 * @param request
	 * @param response
	 */
	public void loadPhotos(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<ContactsPhoto> photos = new ArrayList<ContactsPhoto>();
		ContactsPhoto photo = null;
		String id = request.getParameter("id");
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT photo,remark,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i') createTime  FROM `tbl_contacts_photo` WHERE catId="+id+" ORDER BY createTime DESC";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				photo = new ContactsPhoto();
				photo.setPhoto(rs.getString("photo"));
				photo.setRemark(rs.getString("remark"));
				photo.setCreateTime(rs.getString("createTime"));
				photos.add(photo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(photos), response);
	}
}
