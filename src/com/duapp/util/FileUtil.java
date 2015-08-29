package com.duapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileUtil {

	/**
	 * 剪切文件
	 * 
	 * @param oldFilePathAndName
	 * @param newFilePath
	 * @param newFileName
	 * @return
	 */
	public static boolean cutFile(String oldFilePathAndName, String newFilePath, String newFileName) {
		boolean result = FileUtil.copyFile(oldFilePathAndName, newFilePath, newFileName);
		FileUtil.delFile(oldFilePathAndName);
		return result;
	}
	
	/**
	 * 复制文件
	 * 
	 * @param oldFilePathAndName
	 * @param newFilePath
	 * @param newFileName
	 * @return
	 */
	public static boolean copyFile(String oldFilePathAndName, String newFilePath, String newFileName) {
		boolean result = false;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldFile = new File(oldFilePathAndName);
			File newFolder = new File(newFilePath);
			if(!newFolder.exists()) {
				newFolder.mkdirs();
			}
			if (oldFile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldFilePathAndName); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newFilePath+"/"+newFileName);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				result = true;
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 */
	public static boolean delFile(String filePathAndName) {
		boolean result = false;
		try {
			File myDelFile = new File(filePathAndName);
			result = myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 */
	public static boolean delFile(File file) {
		boolean result = false;
		try {
			result = file.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 检查指定的文件是否存在
	 * 
	 * @param filePathAndName
	 */
	public static boolean isFileExist(String filePathAndName) {
		boolean result = false;
		try {
			if (null != filePathAndName && !"".equals(filePathAndName)) {
				File file = new File(filePathAndName);
				result = file.exists();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 从网络下载文件
	 * 
	 * @param fileUrl
	 * @param newFile
	 * @param minSize
	 * @return
	 */
	public static boolean downloadNet(String fileUrl, File newFile, int minSize){
		boolean result = false;
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URLConnection conn = null;
        InputStream inStream = null;
        FileOutputStream fs = null;
        
        try {
        	URL url = new URL(fileUrl);
            conn = url.openConnection();
            inStream = conn.getInputStream();
            fs = new FileOutputStream(newFile);

            byte[] buffer = new byte[256];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum = bytesum + byteread;
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	try {
        		fs.close();
            	inStream.close();
        	} catch (Exception ex) {
        		ex.printStackTrace();
        	}
        }
        if(bytesum > minSize) {
        	result = true;
        } else {
        	FileUtil.delFile(newFile);
        }
        return result;
    }
}
