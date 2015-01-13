package com.duapp.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadAction extends HttpServlet {

	private static String filePath = "D:/Tomcat/apache-tomcat-6.0.43/concat-photo";
	
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
		request.setCharacterEncoding("utf-8");  //设置编码  
		String fileName = request.getParameter("fileName");
		FileInputStream fips = null;
		try {
			if (null != fileName && !"".equals(fileName)) {
				OutputStream os = response.getOutputStream();  
			    File file = new File(filePath+"/"+fileName);
			    if(file.exists()) {
			    	fips = new FileInputStream(file);  
			        byte[] btImg = readStream(fips);  
			        os.write(btImg);  
			        os.flush(); 
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != fips) {
				fips.close();
			}
		}
	}
	
	/** 
     * 读取管道中的流数据 
     */  
    public byte[] readStream(InputStream inStream) {  
        ByteArrayOutputStream bops = new ByteArrayOutputStream();  
        int data = -1;  
        try {  
            while((data = inStream.read()) != -1){  
                bops.write(data);  
            }  
            return bops.toByteArray();  
        }catch(Exception e){  
            return null;  
        }  
    }  
}
