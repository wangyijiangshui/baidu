package com.duapp.util;

/**
 * 类型常量转换工具
 * 
 * @author Administrator
 *
 */
public class ConvertUtil {

	/**
	 * 股票分类转换
	 * 
	 * @param gpjzqz
	 * @return
	 */
	public static String convertGpfl(int gpjzqz) {
		if (0 == gpjzqz) {
			return "0-垃垃圾";
		} else if (1 == gpjzqz) {
			return "1-垃圾";
		} else if (2 == gpjzqz) {
			return "2-业差";
		} else if (3 == gpjzqz) {
			return "3-业好";
		} else if (4 == gpjzqz) {
			return "4-业差待察";
		} else if (5 == gpjzqz) {
			return "5-业好待察";
		} else if (6 == gpjzqz) {
			return "6-业差趋好";
		} else if (7 == gpjzqz) {
			return "7-业差趋极好";
		} else if (8 == gpjzqz) {
			return "8-业好趋好";
		} else if (9 == gpjzqz) {
			return "9-业好趋极好";
		} else if (10 == gpjzqz) {
			return "10-small";
		} else if (11 == gpjzqz) {
			return "11-middle";
		} else if (12 == gpjzqz) {
			return "12-bigger";
		} else if (13 == gpjzqz) {
			return "13-买入";
		} else if (14 == gpjzqz) {
			return "14-卖出";
		} else {
			return gpjzqz+"";
		}
	}
	
	/**
	 * 任务类型转换
	 * 
	 * @param taskType
	 * @return
	 */
	public static String convertTaskType(int taskType) {
		if (1 == taskType) {
			return "工作";
		} else if (2 == taskType) {
			return "生活";
		} else if (3 == taskType) {
			return "学习";
		} else {
			return "未知";
		}
	}
	
	/**
	 * 任务大小转换
	 * 
	 * @param taskVolume
	 * @return
	 */
	public static String convertTaskVolume(int taskVolume) {
		if (1 == taskVolume) {
			return "临时";
		} else if (2 == taskVolume) {
			return "小型";
		} else if (3 == taskVolume) {
			return "中型";
		} else if (4 == taskVolume) {
			return "大型";
		} else {
			return "未知";
		}
	}
	
	/**
	 * 任务紧急程度转换
	 * 
	 * @param taskUrgency
	 * @return
	 */
	public static String convertTaskUrgency(int taskUrgency) {
		if (1 == taskUrgency) {
			return "一般";
		} else if (2 == taskUrgency) {
			return "紧急";
		} else if (3 == taskUrgency) {
			return "特急";
		} else {
			return "未知";
		}
	}
	
	/**
	 * 任务状态转换
	 * 
	 * @param taskStatus
	 * @return
	 */
	public static String convertTaskStatus(int taskStatus) {
		if (1 == taskStatus) {
			return "没开始";
		} else if (2 == taskStatus) {
			return "进行中";
		} else if (3 == taskStatus) {
			return "完成";
		} else if (4 == taskStatus) {
			return "取消";
		} else if (5 == taskStatus) {
			return "失败";
		} else {
			return "未知";
		}
	}
}
