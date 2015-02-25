package com.duapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

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
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			result = myDelFile.delete();
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
}