package com.duapp.vo;

/**
 * 股票历史备注信息
 * 
 * @author Administrator
 * 
 */
public class StockRemark {

	/** 备注 */
	private String remark;
	/** 记录时间 */
	private String createTime;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
